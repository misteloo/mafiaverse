package ir.greendex.mafia.util.audio_device

import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import ir.greendex.mafia.entity.audio_switch.AudioSwitchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AudioSwitch {
    private lateinit var audioSwitch: AudioSwitch
    private val audioDeviceList by lazy { mutableListOf<AudioSwitchEntity>() }
    private var selectedDevice: AudioSwitchEntity? = null
    fun start(audioSwitch: AudioSwitch) {
        this.audioSwitch = audioSwitch
        audioSwitch.start { audioDevices, selectedAudioDevice ->
            audioDeviceList.clear()
            CoroutineScope(Dispatchers.IO).launch {
                // devices
                audioDevices.forEach {
                    audioDeviceList.add(filterAudioDevices(device = it))
                }
                // selected
                selectedAudioDevice?.let {
                    selectedDevice = filterAudioDevices(device = it)
                }
            }
        }
    }

    fun selectAudioDevice(audioDevice: AudioDevice) {
        if (::audioSwitch.isInitialized) {
            audioSwitch.selectDevice(audioDevice)
            audioSwitch.activate()
        }
    }

    private fun filterAudioDevices(device: AudioDevice): AudioSwitchEntity {
        return when (device) {
            is AudioDevice.Speakerphone -> {
                AudioSwitchEntity(
                    connectedDevices = AudioDeviceEnum.SPEAKER,
                    connectedDevicesName = "اسپیکر بزرگ گوشی",
                    audioDevice = device
                )
            }

            is AudioDevice.BluetoothHeadset -> {
                AudioSwitchEntity(
                    connectedDevices = AudioDeviceEnum.BLUETOOTH,
                    connectedDevicesName = device.name,
                    audioDevice = device
                )
            }

            is AudioDevice.Earpiece -> {
                AudioSwitchEntity(
                    connectedDevices = AudioDeviceEnum.EARPIECES,
                    connectedDevicesName = "اسپیکر کوچک گوشی",
                    audioDevice = device
                )
            }

            is AudioDevice.WiredHeadset -> {
                AudioSwitchEntity(
                    connectedDevices = AudioDeviceEnum.WIRED_HEADSET,
                    connectedDevicesName = "هدست",
                    audioDevice = device
                )
            }
        }
    }
}