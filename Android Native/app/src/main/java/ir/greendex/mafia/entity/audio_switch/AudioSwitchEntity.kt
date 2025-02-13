package ir.greendex.mafia.entity.audio_switch

import com.twilio.audioswitch.AudioDevice
import ir.greendex.mafia.util.audio_device.AudioDeviceEnum

data class AudioSwitchEntity(
    var connectedDevices:AudioDeviceEnum,
    var connectedDevicesName:String,
    var audioDevice:AudioDevice,
    var selected:Boolean = false
)
