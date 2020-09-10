'''
Created on Mar 3, 2017

@author: podalv
'''

import atlas;

atlasInstance = atlas.Atlas("http://localhost:8080");

#test connection
if atlasInstance.test() == True:
    print("OK")

#arr = atlasInstance.getPatientIds("var age = AGE(18 years, MAX)\nvar acetominophen = RX=161\nvar ibuprofen = RX=5640\nvar acet_no_ibu = SEQUENCE($age, INTERSECT($acetominophen, NOT($ibuprofen))*)\nvar ibu_no_acet = SEQUENCE($age, INTERSECT($ibuprofen, NOT($acetominophen))*)\nvar blood_glucose = LABS(\"GLU\")\nvar acet_then_glu = SEQUENCE($acet_no_ibu, $blood_glucose*) + (-7 days, 0)\nOUTPUT($acet_then_glu)")
arr = atlasInstance.getPatientIds("OUTPUT(AGE(88 years, MAX))")
print("Query received")
for key in arr:
    #print(key)
    ti = arr[key]
    patient = atlasInstance.getPatient(key)
    labs = patient.getLabsNumericTimeIntervals("GLU")
    for x in range(0, len(labs)):
        for i in range(0, len(ti)):
            if ((labs[x].start >= ti[i].start) & (labs[x].start <= ti[i].end)):
                print(labs[x].getValue())

