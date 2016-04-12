/**
 *  Welcome Home
 *
 *  Copyright 2016 Dan Rick
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Welcome Home",
    namespace: "Drick172",
    author: "Dan Rick",
    description: "Welcome a person Home. Turn on things. ",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  page(name: "pageOne", title: "When this person Arrives", nextPage: "pageTwo", uninstall: true) {
	section("When this person Arrives") {
        input "presence", "capability.presenceSensor", title: "Whom?", required: true, multiple: true
	}
    section("While the home is Empty or Occupied") {
        input "contactSensors", "capability.contactSensor", title: "Door open/close", required: false, multiple: true
        input "motionSensors", "capability.motionSensor", title: "Motion inside", required: false, multiple: true
	}
  }
  page(name: "pageTwo", title: "Welcome Home This way", install: true, uninstall: true) {    
    section("Welcome Home This way") {
        input "Myswitches", "capability.switch", title: "Turn ON", required: true, multiple: true
    	input "player", "capability.musicPlayer", title: "Play on Sonos", required: false, multiple: false
	}   
  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    subscribe(presence, "presence", myHandler)
	//initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

def myHandler(evt) {
  if("present" == evt.value) {
    Myswitches.setLevel(50)
//    Myswitches.currentstate([delay:2000])
//    Myswitches.setlevel(40)
//    Myswitches.currentstate([delay:2000])
//    Myswitches.setlevel(60)
//    Myswitches.currentstate([delay:2000])
//    Myswitches.setlevel(90)    
  }
}