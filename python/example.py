'''
Created on Mar 3, 2017

@author: podalv
'''

import atlas;

atlasInstance = atlas.Atlas("http://localhost:8080");
#test connection
if atlasInstance.test() == True:
    print("OK")

#output patient ids with code ICD9=250.50
arr = atlasInstance.getPatientIds("ICD9=250.50")
for key in arr:
    print(key)

#output patient ids and time intervals for which the code was true
arr = atlasInstance.getPatientIds("OUTPUT(ICD9=221)")
for key in arr:
    print(key)
    ti = arr[key]
    for i in range(0, len(ti)):
        print("START = " + str(ti[i].start))
        print("END = " + str(ti[i].end))

#get patient with the id = 4     
patient = atlasInstance.getPatient(4)
print(patient.getPatientId())
print(patient.getUniqueIcd9Codes())
print(patient.getUniqueCptCodes())
print(patient.getUniqueRxCodes())
print(patient.getUniqueVisitTypes())
print(patient.getUniqueNoteTypes())
print(patient.getUniqueAtcCodes())
print(patient.getUniqueLabCodes())
print(patient.getUniqueSnomedCodes())
print(patient.getUniquePositiveTerms())
print(patient.getUniqueNegatedTerms())
print(patient.getUniqueFamilyHistoryTerms())
print(patient.getYearsWithData())

#display time intervals and all additional data for family history term 'neurological'
arr = patient.getFamilyHistoryTermTimeIntervals('neurological')
#display how many entries there are for this term
print(len(arr))
print(arr[0].getStart())
print(arr[0].getEnd())
print(arr[0].getNoteId())
print(arr[0].getNoteType())