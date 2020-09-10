#' Connects to ACE instance
#'
#' Attempts to connect to ACE instance using URL:PORT
#' @param url url address of a running ACE instance, usually containing port information
#' @return data frame containing connection information used for all other accessory functions
#'
#' @examples
#' \donttest{ACEsearch.connect("http://localhost:8080")}
#'
ACEsearch.connect <- function(url) {
  tryCatch(status <-httr::GET(paste0(url, "/status")), error=function(e) print("Cannot connect to the specified ACE instance"))
  if (!exists("status")) {
    stop("Could not connect to ACE instance")
  }
  print(status)
  status <- httr::content(status, type="application/json")
  if (status$status != "OK") {
    stop("ACE instance instance error")
  }
  response <- data.frame(1)
  response$url <- url
  response$status <- status$status
  response$dataset <- status$datasetVersion
  response$code <- status$version
  print(paste("Connected to ",url, response$code, response$dataset))
  return (response)
}
