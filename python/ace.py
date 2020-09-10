'''
Created on Mar 2, 2017
Updated on April 26, 2018

@author: podalv
@author: davekale
'''

import requests
import json

class Atlas():

    def __init__(self, url):
        self.url = url
    
    def test(self):
        response = json.loads(requests.get(self.url + "/" + "status").text);
        if response["status"] == "OK":
            return True
        return False
    
    def getPatientIds(self, query):
        request = {'query': query, 'returnPids': True, 'returnTimeIntervals': False, 'returnSurvivalData': False, 'pidCntLimit': 1000000000, 'statisticsLimit': 0, 'binary': False}
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        response = json.loads(requests.post(self.url + "/" + "query", data=json.dumps(request), headers=headers).text)
        if (response['patientIds'] is not None):
            if (len(response['patientIds']) > 0):
                if (len(response['patientIds'][0]) == 1):
                    return [int(i[0]) for i in response['patientIds']]
                else:
                    result = {}
                    for i in range(0, len (response['patientIds'])):
                        patientId = int(response['patientIds'][i][0])
                        value = result.get(patientId, []);
                        value.append(TimeInterval(response['patientIds'][i][1], response['patientIds'][i][2]))
                        result[patientId] = value
                        
                    return result
            
    def getPatient(self, patientId):
        request = {'patientId': patientId, 'icd9': True, 'icd10': True, 'cpt': True, 'rx': True, 'snomed': True, 'notes': True, 'visitTypes':True, 'noteTypes': True, 'encounterDays': True, 'ageRanges': True, 'labs': True, 'vitals': True, 'atc': True}
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        response = json.loads(requests.post(self.url + "/" + "dump", data=json.dumps(request), headers=headers).text)
        return PatientData(response)
                
        
class PatientData():
    
    def __init__(self, response):
        self.patientId = response['patientId']
        self.__parsePatientAttributes(response)
        self.__parseAgeRanges(response)
        self.__parseIcd(response, version='icd9')
        self.__parseIcd(response, version='icd10')
        self.__parseCpt(response)
        self.__parseRx(response)
        self.__parseVisitTypes(response)
        self.__parseNoteTypes(response)
        self.__parseAtc(response)
        self.__parseLabs(response)
        self.__parseVitals(response)
        self.__parseSnomed(response)
        self.__parsePositiveTerms(response)
        self.__parseNegatedTerms(response)
        self.__parseFamilyHistoryTerms(response)
        self.__parseYears(response)

    def __minutesToDays(self, minutes):
        return minutes / (24 * 60.);

    def __minutesToYears(self, minutes):
        return minutes / (24 * 60 * 365.);

    def __parsePatientAttributes(self, response):
        self.death = response['death'] if 'death' in response else -1
        self.gender = response['gender'] if 'gender' in response else None
        self.race = response['race'] if 'race' in response else None
        self.ethnicity = response['ethnicity'] if 'ethnicity' in response else None
        self.recordStart = response['recordStart'] if 'recordStart' in response else None
        self.recordEnd = response['recordEnd'] if 'recordEnd' in response else None

    def __parseAgeRanges(self, response):
        if response['ageRanges'] is not None:
            self.ageRanges = [ self.__minutesToYears(age) for age in response['ageRanges'] ]
        else:
            self.ageRanges = []

    def __parseYears(self, response):
        if (response['yearRanges'] is not None):
            result = []
            data = {}
            for key in response['yearRanges']:
                result.append(key)
                timeIntervals = []
                data[key] = timeIntervals
                pos = 0;
                tiData = response['yearRanges'][key];
                while (pos < len(tiData)):
                    timeIntervals.append(TimeInterval(self.__minutesToDays(int(tiData[pos])), self.__minutesToDays(int(tiData[pos+1]))))
                    pos = pos + 2
            self.years = result
            self.yearRanges = data
        else:
            self.years = []
            self.yearRanges = {}

    def __parseAtc(self, response):
        if (response['atc'] is not None):
            result = []
            atcData = {}
            for key in response['atc']:
                result.append(key)
                rxCodes = []
                atcData[key] = rxCodes
                pos = 0;
                rxData = response['atc'][key];
                while (pos < len(rxData)):
                    rxCodes.append(int(rxData[pos]))
                    pos = pos + 1
            self.uniqueAtc = result
            self.atc = atcData
        else:
            self.uniqueAtc = []
            self.atc = {}        

    def __parseVisitTypes(self, response):
        if (response['visitTypes'] is not None):
            result = []
            visitTypesData = {}
            for key in response['visitTypes']:
                result.append(key)
                timeIntervals = []
                visitTypesData[key] = timeIntervals
                pos = 0;
                visitTypesTiData = response['visitTypes'][key];
                while (pos < len(visitTypesTiData)):
                    timeIntervals.append(TimeInterval(self.__minutesToDays(int(visitTypesTiData[pos])), self.__minutesToDays(int(visitTypesTiData[pos+1]))))
                    pos = pos + 2
            self.uniqueVisitTypes = result
            self.visitTypes = visitTypesData
        else:
            self.uniqueVisitTypes = []
            self.visitTypes = {}        

    def __parseNoteTypes(self, response):
        if (response['noteTypes'] is not None):
            result = []
            noteTypesData = {}
            for key in response['noteTypes']:
                result.append(key)
                timeIntervals = []
                noteTypesData[key] = timeIntervals
                pos = 0;
                noteTypesTiData = response['noteTypes'][key];
                while (pos < len(noteTypesTiData)):
                    timeIntervals.append(TimeInterval(self.__minutesToDays(int(noteTypesTiData[pos])), self.__minutesToDays(int(noteTypesTiData[pos]))))
                    pos = pos + 1
            self.uniqueNoteTypes = result
            self.noteTypes = noteTypesData
        else:
            self.uniqueNoteTypes = []
            self.noteTypes = {}        

    def __parseIcd(self, response, version='icd9'):
        assert(version in ['icd9', 'icd10'])
        result = []
        icdData = {}
        if (response[version] is not None):
            for key in response[version]:
                result.append(key)
                timeIntervals = []
                icdData[key] = timeIntervals
                pos = 0;
                icdTiData = response[version][key];
                while (pos < len(icdTiData)):
                    primary = False
                    if (icdTiData[pos+1] == 'PRIMARY'):
                        primary = True
                    timeIntervals.append(IcdTimeInterval(self.__minutesToDays(int(icdTiData[pos])), self.__minutesToDays(int(icdTiData[pos+1])), primary))
                    pos = pos + 3
        if version == 'icd9':
            self.uniqueIcd9 = result
            self.icd9 = icdData
        elif version == 'icd10':
            self.uniqueIcd10 = result
            self.icd10 = icdData

    def __parseRx(self, response):
        if (response['rx'] is not None):
            result = []
            rxData = {}
            for key in response['rx']:
                result.append(key)
                timeIntervals = []
                rxData[key] = timeIntervals
                pos = 0;
                rxTiData = response['rx'][key];
                while (pos < len(rxTiData)):
                    timeIntervals.append(RxTimeInterval(self.__minutesToDays(int(rxTiData[pos])), self.__minutesToDays(int(rxTiData[pos+1])), rxTiData[pos+2], rxTiData[pos+3]))
                    pos = pos + 4
            self.uniqueRx = result
            self.rx = rxData
        else:
            self.uniqueRx = []
            self.rx = {}
            
    def __parseCpt(self, response):
        if (response['cpt'] is not None):
            result = []
            cptData = {}
            for key in response['cpt']:
                result.append(key)
                timeIntervals = []
                cptData[key] = timeIntervals
                pos = 0;
                cptTiData = response['cpt'][key];
                while (pos < len(cptTiData)):
                    timeIntervals.append(TimeInterval(self.__minutesToDays(int(cptTiData[pos])), self.__minutesToDays(int(cptTiData[pos+1]))))
                    pos = pos + 2
            self.uniqueCpt = result
            self.cpt = cptData
        else:
            self.uniqueCpt = []
            self.cpt = {}

    def __parseSnomed(self, response):
        if (response['snomed'] is not None):
            result = []
            data = {}
            for key in response['snomed']:
                result.append(key)
                timeIntervals = []
                data[key] = timeIntervals
                pos = 0;
                tiData = response['snomed'][key];
                while (pos < len(tiData)):
                    timeIntervals.append(TimeInterval(self.__minutesToDays(int(tiData[pos])), self.__minutesToDays(int(tiData[pos+1]))))
                    pos = pos + 2
            self.uniqueSnomed = result
            self.snomed = data
        else:
            self.uniqueSnomed = []
            self.snomed = {}

    def __parsePositiveTerms(self, response):
        if (response['positiveTerms'] is not None):
            result = []
            data = {}
            for key in response['positiveTerms']:
                result.append(key)
                timeIntervals = []
                data[key] = timeIntervals
                pos = 0;
                tiData = response['positiveTerms'][key];
                while (pos < len(tiData)):
                    timeIntervals.append(TermTimeInterval(self.__minutesToDays(int(tiData[pos])), int(tiData[pos+1]), tiData[pos+2]))
                    pos = pos + 3
            self.uniquePositiveTerms = result
            self.positiveTerms = data
        else:
            self.uniquePositiveTerms = []
            self.positiveTerms = {}

    def __parseNegatedTerms(self, response):
        if (response['negatedTerms'] is not None):
            result = []
            data = {}
            for key in response['negatedTerms']:
                result.append(key)
                timeIntervals = []
                data[key] = timeIntervals
                pos = 0;
                tiData = response['negatedTerms'][key];
                while (pos < len(tiData)):
                    timeIntervals.append(TermTimeInterval(self.__minutesToDays(int(tiData[pos])), int(tiData[pos+1]), tiData[pos+2]))
                    pos = pos + 3
            self.uniqueNegatedTerms = result
            self.negatedTerms = data
        else:
            self.uniqueNegatedTerms = []
            self.negatedTerms = {}
            
    def __parseFamilyHistoryTerms(self, response):
        if (response['fhTerms'] is not None):
            result = []
            data = {}
            for key in response['fhTerms']:
                result.append(key)
                timeIntervals = []
                data[key] = timeIntervals
                pos = 0;
                tiData = response['fhTerms'][key];
                while (pos < len(tiData)):
                    timeIntervals.append(TermTimeInterval(self.__minutesToDays(int(tiData[pos])), int(tiData[pos+1]), tiData[pos+2]))
                    pos = pos + 3
            self.uniqueFamilyHistoryTerms = result
            self.familyHistoryTerms = data
        else:
            self.uniqueFamilyHistoryTerms = []
            self.familyHistoryTerms = {}

    def __parseVitals(self, response):
        if (response['vitals'] is not None):
            result = []
            vitalsData = {}
            for key in response['vitals']:
                result.append(key)
                timeIntervals = []
                vitalsData[key] = timeIntervals
                pos = 0;
                vitalsTiData = response['vitals'][key];
                while (pos < len(vitalsTiData)):
                    timeIntervals.append(NumericTimeInterval(self.__minutesToDays(int(vitalsTiData[pos])), float(vitalsTiData[pos+1])))
                    pos = pos + 2
            self.uniqueVitals = result
            self.vitals = vitalsData
        else:
            self.uniqueVitals = []
            self.vitals = {}

    def __parseLabs(self, response):
        if (response['labs'] is not None):
            result = []
            labsData = {}
            labsDataCalculated = {}
            for key in response['labsRaw']:
                result.append(key)
                timeIntervals = []
                labsData[key] = timeIntervals
                pos = 0;
                numericLabsTiData = response['labsRaw'][key];
                while (pos < len(numericLabsTiData)):
                    timeIntervals.append(NumericTimeInterval(self.__minutesToDays(int(numericLabsTiData[pos])), float(numericLabsTiData[pos+1])))
                    pos = pos + 2
            for key in response['labs']:
                result.append(key)
                timeIntervals = []
                labsDataCalculated[key] = timeIntervals
                pos = 0;
                labsTiData = response['labs'][key];
                while (pos < len(labsTiData)):
                    timeIntervals.append(CalculatedLabsTimeInterval(self.__minutesToDays(int(labsTiData[pos])), labsTiData[pos+1]))
                    pos = pos + 2

            self.uniqueLabs = result
            self.labsCalculated = labsDataCalculated
            self.labs = labsData
        else:
            self.uniqueLabs = []
            self.labsCalculated = {}
            self.labs = {}
        

    def getPatientId(self):
        return self.patientId

    def getRecordStart(self):
        return self.recordStart
    
    def getRecordEnd(self):
        return self.recordEnd
    
    def getGender(self):
        return self.gender
    
    def getRace(self):
        return self.race
    
    def getEthnicity(self):
        return self.ethnicity

    def getPatientAgeRanges(self):
        return self. ageRanges
    
    @property
    def died(self):
        if self.death != -1:
            return True
        else:
            return False

    def hasDied(self):
        if self.death != -1:
            return True
        else:
            return False

    def getDeath(self):
        return self.death
    
    def getUniqueIcd9Codes(self):
        return self.uniqueIcd9
    
    def getIcd9TimeIntervals(self, icd9Code):
        return self.icd9[icd9Code]

    def getUniqueIcd10Codes(self):
        return self.uniqueIcd10
    
    def getIcd10TimeIntervals(self, icd10Code):
        return self.icd10[icd10Code]

    def getUniqueCptCodes(self):
        return self.uniqueCpt

    def getCptTimeIntervals(self, cptCode):
        return self.cpt[cptCode]

    def getUniqueRxCodes(self):
        return self.uniqueRx

    def getRxTimeIntervals(self, rxCode):
        return self.rx[rxCode]

    def getUniqueVisitTypes(self):
        return self.uniqueVisitTypes

    def getVisitTypeTimeIntervals(self, visitType):
        return self.visitTypes[visitType]

    def getUniqueNoteTypes(self):
        return self.uniqueNoteTypes

    def getNoteTypeTimeIntervals(self, noteType):
        return self.noteTypes[noteType]

    def getUniqueAtcCodes(self):
        return self.uniqueAtc

    def getAtcRxNorms(self, atcCode):
        return self.atc[atcCode]

    def getUniqueLabCodes(self):
        return self.uniqueLabs

    def getLabsComputedTimeIntervals(self, labCode):
        return self.labsCalculated[labCode]

    def getLabsNumericTimeIntervals(self, labCode):
        return self.labs[labCode]

    def getLabsTimeIntervals(self, labCode):
        assert(labCode not in self.labs or labCode not in self.labsCalculated)
        if labCode in self.labs:
            return self.labs[labCode]
        else:
            return self.labsCalculated[labCode]

    def getUniqueSnomedCodes(self):
        return self.uniqueSnomed

    def getSnomedTimeIntervals(self, snomedCode):
        return self.snomed[snomedCode]

    def getUniquePositiveTerms(self):
        return self.uniquePositiveTerms

    def getPositiveTermTimeIntervals(self, term):
        return self.positiveTerms[term]

    def getUniqueNegatedTerms(self):
        return self.uniqueNegatedTerms

    def getNegatedTermTimeIntervals(self, term):
        return self.negatedTerms[term]
    
    def getUniqueFamilyHistoryTerms(self):
        return self.uniqueFamilyHistoryTerms

    def getUniqueVitals(self):
        return self.uniqueVitals

    def getVitalTimeIntervals(self, vital):
        return self.vitals[vital]

    def getFamilyHistoryTermTimeIntervals(self, term):
        return self.familyHistoryTerms[term]

    def getYearsWithData(self):
        return self.years
    
    def getYearTimeIntervals(self, year):
        return self.yearRanges[year]
    
class TimeInterval():

    def __init__(self, start, end):
        self.start = start
        self.end = end

    def getStart(self):
        return self.start
        
    def getEnd(self):
        return self.end

class IcdTimeInterval():

    def __init__(self, start, end, primaryDiagnosis):
        self.start = start
        self.end = end
        self.primary = primaryDiagnosis

    def getStart(self):
        return self.start
        
    def getEnd(self):
        return self.end
    
    def isPrimary(self):
        return self.primary

class RxTimeInterval():

    def __init__(self, start, end, drugRoute, drugStatus):
        self.start = start
        self.end = end
        self.status = drugStatus
        self.route = drugRoute

    def getStart(self):
        return self.start
        
    def getEnd(self):
        return self.end
    
    def getDrugStatus(self):
        return self.status

    def getDrugRoute(self):
        return self.route

class NumericTimeInterval():

    def __init__(self, start, numericValue):
        self.start = start
        self.numericValue = numericValue

    @property
    def end(self):
        return self.start

    def getStart(self):
        return self.start
        
    def getEnd(self):
        return self.end
    
    @property
    def value(self):
        return self.numericValue

    def getValue(self):
        return self.value

class CalculatedLabsTimeInterval():

    def __init__(self, start, calculatedValue):
        self.start = start
        self.calculatedValue = calculatedValue

    @property
    def end(self):
        return self.start

    def getStart(self):
        return self.start
        
    def getEnd(self):
        return self.end
    
    @property
    def value(self):
        return self.calculatedValue

    def getValue(self):
        return self.value

class TermTimeInterval():

    def __init__(self, start, noteId, noteType):
        self.start = start
        self.noteId = noteId
        self.noteType = noteType

    @property
    def end(self):
        return self.start

    def getStart(self):
        return self.start
        
    def getEnd(self):
        return self.end
    
    def getNoteId(self):
        return self.noteId
    
    def getNoteType(self):
        return self.noteType
