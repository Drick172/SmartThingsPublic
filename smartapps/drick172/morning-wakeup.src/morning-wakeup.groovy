/**
 *  Morning wakeup
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
    name: "Morning wakeup",
    namespace: "Drick172",
    author: "Dan Rick",
    description: "Morning Wakeup Routine",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  page(name: "pageOne", title: "How do I know to start your morning?", nextPage: "pageTwo", uninstall: true) {
	section("What Happens in the morning") {
    	input "presence", "capability.presenceSensor", title: "Who might be home", required: false, multiple: true
    	input "NOTpresence", "capability.presenceSensor", title: "Who is not home", required: false, multiple: true
        input "motionSensors", "capability.motionSensor", title: "Motion in area", required: false, multiple: true
  	}
  }
  page(name: "pageTwo", title: "How do I say Good Morning?", nextPage: "pageThree", uninstall: true) {
  	section("What do I turn on?") {
    	input "myswitch", "capability.switch", title: "Turn on", required: false, multiple: true
        input "Sonos", "capability.musicPlayer", title: "Sonos Music Player", required: false, multiple: false
    }
    section("Sonos options", hideable: true, hidden: true) {
		input "volume", "number", title: "Set the volume volume", description: "0-100%", required: false
        input "resumePlaying", "bool", title: "Resume currently playing music", required: false, defaultValue: true
		href "chooseTrack", title: "Or play this music or radio station", description: song ? state.selectedSong?.station : "Tap to set", state: song ? "complete" : "incomplete"

	}
  }
  page(name: "pageThree", title: "When is morning?", install: true, uninstall: true) {
  	section("When and what time") {
//		href "timeIntervalInput", title: "Only during a certain time", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : "incomplete"
		input "days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
		options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
//		input "modes", "mode", title: "Only when mode is", multiple: true, required: false
		input "oncePerDay", "bool", title: "Only once per day", required: false, defaultValue: false
        input "frequency", "decimal", title: "Minimum time between actions (defaults to every event)", description: "Minutes", required: false
	}
    section([mobileOnly:true]) {
			label title: "Assign a name", required: false
    }
  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(presenceIN, "presence", myHandler)
    subscribe(presenceOUT, "NOTpresence", myHandler)
    subscribe(MotionTRUE, "motionSensors", myHandler)
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
//  if("present" == evt.value) {
//    Myswitches.setLevel(50)


//    Myswitches.currentstate([delay:2000])
//    Myswitches.setlevel(40)
//    Myswitches.currentstate([delay:2000])
//    Myswitches.setlevel(60)
//    Myswitches.currentstate([delay:2000])
//    Myswitches.setlevel(90)    
//  }
}

def chooseTrack() {
	dynamicPage(name: "chooseTrack") {
		section{
			input "song","enum",title:"Play this track", required:true, multiple: false, options: songOptions()
		}
	}
}
private frequencyKey(evt) {
	//evt.deviceId ?: evt.value
	"lastActionTimeStamp"
}

private dayString(Date date) {
	def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
	if (location.timeZone) {
		df.setTimeZone(location.timeZone)
	}
	else {
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
	}
	df.format(date)
}

private oncePerDayOk(Long lastTime) {
	def result = true
	if (oncePerDay) {
		result = lastTime ? dayString(new Date()) != dayString(new Date(lastTime)) : true
		log.trace "oncePerDayOk = $result"
	}
	result
}

def appTouchHandler(evt) {
	takeAction(evt)
}

private takeAction(evt) {

	loadText()

	if (song) {
		sonos.playSoundAndTrack(state.sound.uri, state.sound.duration, state.selectedSong, volume)
	}
	else if (resumePlaying){
		sonos.playTrackAndResume(state.sound.uri, state.sound.duration, volume)
	}
	else if (volume) {
		sonos.playTrackAtVolume(state.sound.uri, volume)
	}
	else {
		sonos.playTrack(state.sound.uri)
	}

	if (frequency || oncePerDay) {
		state[frequencyKey(evt)] = now()
	}
}
private songOptions() {

	// Make sure current selection is in the set

	def options = new LinkedHashSet()
   	if (state.selectedSong?.station) {
	  options << state.selectedSong.station
	}
	else if (state.selectedSong?.description) {
		// TODO - Remove eventually? 'description' for backward compatibility
		options << state.selectedSong.description
	}

	// Query for recent tracks
	def states = sonos.statesSince("trackData", new Date(0), [max:30])
	def dataMaps = states.collect{it.jsonValue}
	options.addAll(dataMaps.collect{it.station})

	log.trace "${options.size()} songs in list"
	options.take(20) as List
}

private saveSelectedSong() {
	try {
		def thisSong = song
		log.info "Looking for $thisSong"
		def songs = sonos.statesSince("trackData", new Date(0), [max:30]).collect{it.jsonValue}
		log.info "Searching ${songs.size()} records"

		def data = songs.find {s -> s.station == thisSong}
		log.info "Found ${data?.station}"
		if (data) {
			state.selectedSong = data
			log.debug "Selected song = $state.selectedSong"
		}
		else if (song == state.selectedSong?.station) {
			log.debug "Selected existing entry '$song', which is no longer in the last 20 list"
		}
		else {
			log.warn "Selected song '$song' not found"
		}
	}
	catch (Throwable t) {
		log.error t
	}
}