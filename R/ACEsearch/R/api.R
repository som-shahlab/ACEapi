#' Returns the status of the ACE
#'
#' @param connection connection object returned from connect(url) function
#' @return data frame containing patient IDs and time intervals (optional)
#'
#' @examples
#' \donttest{ACEsearch.status(ACEsearch.connect('http://localhost:8080'))}
#'
#'
ACEsearch.status <- function(connection) {
  response <- httr::POST(url = paste0(connection$url,'/status'))
  json_response <- httr::content(response, type="application/json")
  if (!is.null(json_response$errorMessage)) {
    stop(json_response$errorMessage)
  }

  return(json_response)
}

#' Returns the statistics information
#'
#' @param connection connection object returned from connect(url) function
#' @param patient_id numerical id of the patient
#' @return TRUE or FALSE
#'
#' @examples
#' \donttest{ACEsearch.contains(ACEsearch.connect('http://localhost:8080'), 123)}
#'
#'
ACEsearch.contains <- function(connection, patient_id) {
  request <- paste0('{"patientId":', patient_id, '}')
  response <- httr::POST(url = paste0(connection$url,'/contains_patient'), body=request)
  json_response <- httr::content(response, type="application/json")

  return(json_response$response)
}

#' Dumps patient from ACE to a file on disk
#'
#' @param connection connection object returned from connect(url) function
#' @param patient_id numerical id of the patient
#' @param path path where to store the generated files
#' @param selection_query returns only the part of patient's data that intersects with the result of the selection_query
#' @param contains_start the dumped time interval's start has to be intersecting the selection_query
#' @param contains_end the dumped time interval's end has to be intersecting the selection_query
#' @return data frame containing patient IDs and time intervals (optional)
#'
#' @examples
#' \donttest{ACEsearch.dump(ACEsearch.connect('http://localhost:8080'), 123, '/path/to/dump/files/')}
#' \donttest{ACEsearch.dump(ACEsearch.connect('http://localhost:8080'), 123, '/path/', 'ICD9=250.50', TRUE, TRUE)}
#'
#'
ACEsearch.dump <- function(connection, patient_id, path, selection_query=NULL, contains_start=FALSE, contains_end=FALSE) {
  request <- paste0('{"patientId":', patient_id, ', "icd9":true, "icd10": true, "departments":true, "cpt":true, "rx":true, "snomed": true, "notes": true, "visitTypes": true, "noteTypes": true, "encounterDays": true, "ageRanges": true, "labs": true, "vitals": true, "atc": true', ',"selectionQuery":"',selection_query, '", "containsStart":', contains_start, ', "containsEnd": ', contains_end, '}')
  if (ACEsearch.contains(connection, patient_id) == FALSE) {
    stop(paste0("Patient with id ", patient_id, " does not exist"))
  }
  response <- httr::POST(url = paste0(connection$url,'/dump'), body=request, write_disk(file.path(path, paste0(patient_id, '.json')), overwrite=TRUE))
}
