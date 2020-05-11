package battlecity.movable

import battlecity.view.City
import battlecity.flash.Spawn
import battlecity.util.Audio
import java.awt.event.*

// 玩家坦克类
internal class Player(val group: Group, x: Int, y: Int = 419, direction: Direction = Direction.UP)
    : Tank(x, y, direction), Runnable, KeyListener {
    internal var type = Type.TYPE1

    // 用于标记坦克的移动方向
    private var move = BooleanArray(4)
    // 用于标记坦克是否开火
    private var fire = false
    // 用于标记坦克是否打滑
    private var slip = false

    // 定义坦克的闪烁时间
    internal var flashTime = 33
    // 定义坦克的无敌时间
    internal var helmetTime = 158
    // 定义坦克生成器
    internal val spawn = Spawn(x, 417)

    // 用于统计所击毁的各类型敌方坦克数目
    internal var points = IntArray(5)
    // 用于统计击毁敌方坦克所获得各项分数
    internal var scores = IntArray(4)
    // 用于统计玩家总分
    internal var scoreTotal = 0L
    // 用于控制奖励玩家一条命的分数上限
    internal var canReward = true
    // 得分榜绘制玩家得分的间隔时间
    internal var interval = intArrayOf(9, 30)
    // 定义得分榜用于数字动态效果的缓冲变量
    internal var counter = IntArray(4){ 1 }

    // 用于控制玩家的移动步数
    private var moveStep = 1
    // 用于控制线程的休眠时间
    private var sleepTime = 0L

    // 定义玩家的按键布局
    private var keyCodes = arrayOf(
            intArrayOf(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE),
            intArrayOf(KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD6, KeyEvent.VK_CONTROL))

    // 初始化模块
    init {
        halfW = 13
        halfH = 13
        health = 2
        moving = false
        City.players.add(this)
        Thread(this).start()
    }

    internal fun letStay() {
        moving = false
        move = BooleanArray(4)
    }

    // 重置玩家坦克
    internal fun onReset() {
        alive = true
        health = 2

        points = IntArray(5)
        scores = IntArray(4)
        scoreTotal = 0
        canReward = true

        onHit()
    }

    // 玩家坦克死亡时重置相关变量
    internal fun onHit() {
        halfW = 13
        halfH = 13
        type = Type.TYPE1

        nextStage()
    }

    // 玩家坦克进入下一关时重置相关变量
    internal fun nextStage() {
        if (alive) {
            direction = Direction.UP
            x = if (group == Group.PLAYER1) 176 else 304
            y = 432 - halfH

            slip = false
            moving = false
            fire = false
            flashing = false
            move = BooleanArray(4)

            flashTime = 33
            helmetTime = 158
            spawn.existTime = 56

            moveStep = 1
            sleepTime = 0L

            interval = intArrayOf(9, 30)
            counter = IntArray(4){ 1 }
        }
    }

    // 强化玩家坦克
    internal fun strengthen() {
        type = when (type) {
            Type.TYPE1 -> {
                halfH = 16
                Type.TYPE2
            }
            Type.TYPE2 -> {
                halfH = 15
                Type.TYPE3
            }
            else -> {
                halfW = 14
                Type.TYPE4
            }
        }
    }

    // 实现玩家坦克的移动功能
    private fun move() {
        if (alive && !flashing) {
            if (touchIce) {
                moveStep = 29
                sleepTime = 20L

                if (slip && !Audio.start) {
                    slip = false
                    Audio.play("TouchIce")
                }
            } else {
                moveStep = 1
                sleepTime = 0L
            }

            when {
                move[0] -> {
                    moving = true
                    direction = Direction.UP
                    for (i in 1..moveStep) {
                        if (onMoveUp() && y - speed >= 32) {
                            y -= speed
                        }
                        Thread.sleep(sleepTime)
                    }
                }
                move[1] -> {
                    moving = true
                    direction = Direction.DOWN
                    for (i in 1..moveStep) {
                        if (onMoveDown() && y + speed <= 416) {
                            y += speed
                        }
                        Thread.sleep(sleepTime)
                    }
                }
                move[2] -> {
                    moving = true
                    direction = Direction.LEFT
                    for (i in 1..moveStep) {
                        if (onMoveLeft() && x - speed >= 48) {
                            x -= speed
                        }
                        Thread.sleep(sleepTime)
                    }
                }
                move[3] -> {
                    moving = true
                    direction = Direction.RIGHT
                    for (i in 1..moveStep) {
                        if (onMoveRight() && x + speed <= 432) {
                            x += speed
                        }
                        Thread.sleep(sleepTime)
                    }
                }
            }
        }
    }


    // 实现玩家坦克的开火功能
    private fun fire() {
        // 当玩家坦克处于开火状态时
        if (alive && fire) {
            var shellTotal = 0

            City.shells.forEach {
                if (it.group == group && it.alive) {
                    shellTotal++
                }
            }

            if (shellTotal == 0 || (type.ordinal > 1 && shellTotal == 1)) {
                City.shells.add(when (direction) {
                    Direction.UP -> Shell(Direction.UP, x, y - halfH, group, type)
                    Direction.DOWN -> Shell(Direction.DOWN, x, y + halfH - 8, group, type)
                    Direction.LEFT -> Shell(Direction.LEFT, x - halfH, y, group, type)
                    Direction.RIGHT -> Shell(Direction.RIGHT, x + halfH - 8, y, group, type)
                })

                Audio.play("Fire")
            }
            fire = false
        }
    }

    override fun run() {
        while (true) {
            if (alive && spawn.existTime == 0
                    && !City.pause && !City.destroy) {
                move()

                fire()

                Thread.sleep(18L)
            } else {
                Thread.sleep(100L)
            }
        }
    }

    override fun keyPressed(e: KeyEvent) {
        val index = group.ordinal
        when (e.keyCode) {
            keyCodes[index][0] -> move[0] = true
            keyCodes[index][1] -> move[1] = true
            keyCodes[index][2] -> move[2] = true
            keyCodes[index][3] -> move[3] = true
        }
    }
    override fun keyReleased(e: KeyEvent) {
        val index = group.ordinal
        when (e.keyCode) {
            keyCodes[index][0] -> move[0] = false
            keyCodes[index][1] -> move[1] = false
            keyCodes[index][2] -> move[2] = false
            keyCodes[index][3] -> move[3] = false
            keyCodes[index][4] -> fire = true
        }
        moving = false
        slip = false
    }
    override fun keyTyped(e: KeyEvent) {}
}