package battlecity.view

import battlecity.flash.*
import battlecity.movable.*
import battlecity.stage.Stage
import battlecity.util.*
import java.awt.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JPanel
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

// 战场类
internal class City : JPanel(), Runnable, FocusListener {
    // 向战场添加一辆敌方坦克
    private fun enemySpawn() {
        // 当敌方坦克生成器的生成时间等于 0 时，向战场添加一辆敌方坦克
        if (Enemy.spawn.existTime == 0) {
            Enemy()
        }
    }

    // 执行初始化的相关操作
    init {
        // 设置战场背景颜色
        background = Color(0x7F7F7F)
    }

    // 绘制战场
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // 当游戏结束倒计时大于0时
        if (Animator.countdown[1] > 0) {
            // 绘制信息面板
            Animator.drawInfo(g)

            // 绘制基地
            Animator.drawBase(g)

            // 绘制地图
            Animator.drawMap(g)

            // 绘制玩家坦克
            Animator.drawPlayer(g)

            // 绘制敌方坦克
            Animator.drawEnemy(g)

            // 绘制炮弹
            Animator.drawShell(g)

            // 绘制树林
            Animator.drawWood(g)

            // 绘制分数
            Animator.drawScore(g)

            // 绘制爆炸
            Animator.drawExplosion(g)

            // 绘制道具
            Animator.drawPower(g)

            // 绘制暂停符号
            Animator.drawPauseSign(g)

            // 绘制GameOver符号
            Animator.drawGameOverSign(g)

            // 绘制游戏开始界面
            Animator.drawStageCover(g)
        } else {
            background = Color.BLACK

            // 绘制得分榜
            Animator.drawScoreBoard(g)

            // 进入下一关
            nextStage()
        }
    }


    // 重置战场的相关变量
    internal fun onReset() {
        // 重置道具
        power.onReset()
        // 取消暂停标记
        pause = false
        // 取消基地被毁标记
        destroy = false

        // 将敌方坦克集合中的坦克设为死亡状态
        enemies.forEach { it.alive = false }
        // 清空炮弹集合
        shells.clear()
        // 清空敌方坦克集合
        enemies.clear()
        // 清空爆炸集合
        explosions.clear()

        // 重置敌方坦克总数
        Config.enemySum = 20
        // 重置敌方坦克生成器的初始位置
        Enemy.spawn = Spawn(240, 32)
        // 设置战场的背景颜色
        background = Color(0x7F7F7F)

        // 重置动画的相关变量
        Animator.onReset()
    }

    // 进入下一关
    private fun nextStage() {
        // 当基地未损坏并且有玩家坦克存活时
        if (!destroy && (players[0].alive || players.size == 2 && players[1].alive) && Animator.interval[1] == 0) {
            // 将当前的地图编号加一
            if (Config.stageIndex < 35) {
                ++Config.stageIndex
            } else {
                Config.stageIndex = 1
            }
            // 重置玩家的相关信息
            players.forEach {
                it.nextStage()
                // 重置玩家击毁的各类型坦克的数目
                it.points = IntArray(5)
                // 重置玩家击毁各类型坦克所的得分
                it.scores = IntArray(4)
            }
            // 播放音频
            Audio.start = true
            // 加载地图
            stage.loadMap()
            // 重置战场的相关变量
            onReset()
        }
    }

    // 当玩家总分大于 2000 时奖励一条命
    private fun rewardHealth(player: Player): Boolean {
        // 玩家坦克总分大于20000时加一条命
        if (player.canReward && player.scoreTotal >= 20000) {
            player.canReward = false
            ++player.health
            Audio.play("RaiseHealth")
            return true
        }
        return false
    }
    // 当玩家坦克触碰道具时
    private fun isTouchPowerUp() {
        players.forEach {
            if (power.exist) {
                // 当玩家坦克与道具相交时
                if (it.alive && power.exist && it.getRect().intersects(power.getRect())) {
                    // 标志战场上不存在道具
                    power.exist = false
                    // 玩家坦克加500分
                    it.scoreTotal += 500
                    scores.add(Score(power.x + 16, power.y + 15, 5))

                    // 根据玩家坦克触碰的道具类型进行分类讨论
                    when (power.type) {
                        // 当道具是钢盔时
                        Power.Type.Helmet -> {
                            it.helmetTime = 612
                            if (!rewardHealth(it)) {
                                Audio.play("TouchPowerUp")
                            }
                        }
                        // 当道具是计时器时
                        Power.Type.CLOCK -> {
                            power.clockTime = 582
                            if (!rewardHealth(it)) {
                                Audio.play("TouchPowerUp")
                            }
                        }
                        // 当道具是铲子时
                        Power.Type.SHOVEL -> {
                            power.shovelTime = 1221
                            stage.changeBaseWallToSteel()
                            if (!rewardHealth(it)) {
                                Audio.play("TouchPowerUp")
                            }
                        }
                        // 当道具是星星时
                        Power.Type.STAR -> {
                            it.strengthen()
                            if (!rewardHealth(it)) {
                                Audio.play("TouchPowerUp")
                            }
                        }
                        // 当道具是炸弹时
                        Power.Type.BOMB -> {
                            enemies.forEach { enemy ->
                                enemy.alive = false
                                explosions.add(Explosion(enemy.x, enemy.y, 48))
                            }
                            if (!rewardHealth(it)) {
                                Audio.play("TouchPowerUp")
                                Audio.play("EnemyExplode")
                            }
                        }
                        // 当道具是坦克图腾时
                        Power.Type.TANK -> {
                            ++it.health
                            if (!rewardHealth(it)) {
                                Audio.play("RaiseHealth")
                            }
                        }
                    }
                }
            }
        }

        // 计时器道具效果时间检测
        if (!pause && power.clockTime > 0) {
            if (power.clockTime == 582) {
                enemies.forEach { it.letStay() }
            } else if (power.clockTime == 1) {
                enemies.forEach { it.letMove() }
            }
            --power.clockTime
        }
    }

    // 保存玩家总分
    internal fun saveRecord() {
        if (players.size == 1 && !players[0].canReward) {
            Config.hiScore = players[0].scoreTotal
        } else if (players.size == 2) {
            if (players[0].scoreTotal >= players[1].scoreTotal && !players[0].canReward) {
                Config.hiScore = players[0].scoreTotal
            } else if (players[0].scoreTotal < players[1].scoreTotal && !players[1].canReward) {
                Config.hiScore = players[1].scoreTotal
            }
        }
    }

    override fun run() {
        while (true) {
            // 刷新战场面板
            repaint()

            // 生成敌方坦克
            enemySpawn()

            // 检测是否吃到道具
            isTouchPowerUp()

            // 动画闪烁
            Animator.flash()

            Thread.sleep(20L)
        }
    }

    // 当战场面板失去焦点时
    override fun focusLost(e: FocusEvent) {
        // 标记战场面板失去焦点
        gainFocus = false
        // 战场面板处于非暂停状态时
        if (!pause) {
            // 暂停敌方坦克
            enemies.forEach { it.letStay() }
            // 暂停玩家坦克
            players.forEach { it.letStay() }
            // 暂停炮弹
            shells.forEach { it.letStay() }
        }
    }

    // 当战场面板获得焦点时
    override fun focusGained(e: FocusEvent) {
        // 标记战场面板获得焦点
        gainFocus = true
        // 当战场面板处于非暂停状态时
        if (!pause) {
            // 恢复敌方坦克行动
            enemies.forEach { it.letMove() }
            // 恢复炮弹运动
            shells.forEach { it.letMove() }
        }
    }


    companion object {
        // 用于标记游戏是否处于暂停状态
        internal var pause = false
        // 用于标基地是否被击毁
        internal var destroy = false
        // 用于标记窗口是否取得焦点
        internal var gainFocus = true

        // 创建关卡
        internal val stage = Stage()
        // 创建道具
        internal val power = Power()

        // 创建玩家坦克集合
        internal val players = ArrayList<Player>()
        // 创建敌方坦克集合
        internal val enemies = CopyOnWriteArrayList<Enemy>()
        // 创建炮弹集合
        internal val shells = CopyOnWriteArrayList<Shell>()
        // 创建爆炸集合
        internal val explosions = CopyOnWriteArrayList<Explosion>()
        // 创建分数集合
        internal val scores = CopyOnWriteArrayList<Score>()
    }
}