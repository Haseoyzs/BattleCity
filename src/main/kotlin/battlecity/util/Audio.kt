package battlecity.util

import battlecity.view.City
import sun.audio.AudioPlayer
import sun.audio.AudioStream
import sun.audio.ContinuousAudioDataStream

// 音频播放类
internal object Audio: Runnable{
    // 开始播放音频的标记
    internal var start = true
    private val enemyMoveData = AudioStream(Audio::class.java.getResourceAsStream("/Audio/EnemyMove.au")).data
    private val playerMoveData = AudioStream(Audio::class.java.getResourceAsStream("/Audio/PlayerMove.au")).data
    private val enemyMove = ContinuousAudioDataStream(enemyMoveData)
    private val playerMove = ContinuousAudioDataStream(playerMoveData)

    override fun run() {
        while (true) {
            if (start) {
                AudioPlayer.player.stop(enemyMove)
                AudioPlayer.player.stop(playerMove)
                AudioPlayer.player.start(Audio::class.java.getResourceAsStream("/Audio/OP.au"))
                Thread.sleep(4500L)
                start = false
            }
            if ((City.players[0].moving || City.players.size == 2 && City.players[1].moving) && Animator.countdown[1] > 0) {
                AudioPlayer.player.stop(enemyMove)
                AudioPlayer.player.start(playerMove)
            } else {
                AudioPlayer.player.stop(playerMove)

                if (!City.destroy && !City.pause && City.gainFocus && (City.players[0].alive || City.players.size == 2 && City.players[1].alive)
                        && (!City.enemies.isEmpty() || Config.enemySum != 0)) {
                    AudioPlayer.player.start(enemyMove)
                } else {
                    AudioPlayer.player.stop(enemyMove)
                }
            }
            Thread.sleep(300L)
        }
    }

    internal fun play(fileName: String) {
        if (!start && Animator.countdown[1] > 0) {
            AudioPlayer.player.start(Audio::class.java.getResourceAsStream("/Audio/$fileName.au"))
        }
    }
}