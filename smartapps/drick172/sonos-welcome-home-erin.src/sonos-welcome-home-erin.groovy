/**
 *  Sonos Welcome Home Erin
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
    name: "Sonos Welcome Home Erin",
    namespace: "Drick172",
    author: "Dan Rick",
    description: "Play Welcome Home on Sonos",
    category: "Fun & Social",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  section("Do this") {
    input "player", "capability.musicPlayer", title: "music player", required: true, multiple: false
  }
  section("When This Happens") {
    input "When", "capability.motionSensor", title: "Where", required: true, multiple: false
    //input "When", "capability.switch", title: "When", required: true, multiple: true
  }
}


def installed() {
  subscribe(frontDoor, "motion", myHandler)
}

def myHandler(evt) {
 // if(true) {
 //   player.playText("Andrew, We are going out for your birthday, with Quinn, and Andy. Have a GREAT time!")
 // }
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

// TODO: implement event handlers