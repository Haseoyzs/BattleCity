package battlecity.movable

import battlecity.flash.Spawn
import battlecity.util.*
import battlecity.view.City

// 敌方坦克类
internal class Enemy(x: Int = 240, y: Int = 32, direction: Direction = Direction.DOWN) : Tank(x, y, direction), Runnable {
    // 随机生成敌方坦克类型
    internal val type = Dynamic.randomTankType()
    // 创建炮弹
    private var shell = Shell(Direction.DOWN, x, y, Group.ENEMY, type)

    // 执行初始化的相关操作
    init {
        // 根据坦克类型设置坦克速度
        if (type == Type.TYPE2) {
            speed = 4
        } else if (type == Type.TYPE4) {
            health = 3
        }

        // 将坦克设为道具坦克
        if (Config.enemySum in 3..17 step 7) {
            flashing = true
            City.power.exist = false
        }

        // 当战场处于计时器效果时间或者战场失去焦点时
        if (City.power.clockTime > 0 || !City.gainFocus) {
            // 暂停坦克
            letStay()
        }

        // 设置坦克的生成位置
        this.x = spawn.x
        spawn.x = if (spawn.x == 432) 48 else spawn.x + 192
        spawn.existTime = if (Config.enemySum in (21 - Config.maxCount)..20) 188 else 102

        // 将敌方坦克总数减一
        --Config.enemySum
        shell.alive = false

        // 将坦克添加到坦克集合中
        City.enemies.add(this)
        // 启动坦克线程
        Thread(this).start()
    }

    // 暂停坦克
    internal fun letStay() {
        speed = 0
        moving = false
    }

    // 恢复坦克移动
    internal fun letMove() {
        speed = if (type == Type.TYPE2) 4 else 2
        moving = true
    }

    // 实现敌方坦克的开火功能
    private fun fire() {
        if (!shell.alive && alive && City.power.clockTime == 0 && !City.pause && City.gainFocus) {
            shell = when (direction) {
                Direction.UP -> Shell(Direction.UP, x, y - 15, Group.ENEMY, type)
                Direction.DOWN -> Shell(Direction.DOWN, x, y + 7, Group.ENEMY, type)
                Direction.LEFT -> Shell(Direction.LEFT, x - 15, y, Group.ENEMY, type)
                Direction.RIGHT -> Shell(Direction.RIGHT, x + 7, y, Group.ENEMY, type)
            }
            City.shells.add(shell)
        }
    }

    // 射击玩家坦克
    private fun shootPlayer() {
        if (alive && City.power.clockTime == 0 && !City.pause && City.gainFocus) {
            City.players.forEach {
                if (it.alive) {
                    if (it.x in x - 160..x + 160 || it.y in y - 160..y + 160) {
                        when {
                            it.x in x - 16..x + 16 -> {
                                when {
                                    it.y > y -> {
                                        if (Dynamic.randomBranch()) {
                                            direction = Direction.DOWN
                                            fire()
                                        }
                                    }
                                    it.y < y -> {
                                        if (Dynamic.randomBranch()) {
                                            direction = Direction.UP
                                            fire()
                                        }
                                    }
                                }
                            }
                            it.y in y - 16..y + 16 -> {
                                when {
                                    it.x > x -> {
                                        if (Dynamic.randomBranch()) {
                                            direction = Direction.RIGHT
                                            fire()
                                        }
                                    }
                                    it.x < x -> {
                                        if (Dynamic.randomBranch()) {
                                            direction = Direction.LEFT
                                            fire()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 射击基地
    private fun shootBase() {
        if (alive && City.power.clockTime == 0 && !City.pause && City.gainFocus) {
            when {
                y > 400 -> {
                    when {
                        x < 208 -> {
                            if (Dynamic.randomBranch()) {
                                direction = Direction.RIGHT
                                fire()
                            } else {
                                direction = Dynamic.randomDirection()
                            }
                        }
                        x > 256 -> {
                            if (Dynamic.randomBranch()) {
                                direction = Direction.LEFT
                                fire()
                            } else {
                                direction = Dynamic.randomDirection()
                            }
                        }
                    }
                }
            }
        }
    }


    // 实现敌方坦克的自主移动
    private fun move() {
        if (alive && City.power.clockTime == 0 && !City.pause && City.gainFocus) {
            loop@for (i in 1..2) {
                for (j in 1..16) {
                    when (direction) {
                        Direction.UP -> {
                            when {
                                y - speed >= 32 && onMoveUp() -> y -= speed
                                Dynamic.randomBranch() -> {
                                    fire()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                                moving && !City.pause -> {
                                    direction = Dynamic.randomWithoutUp()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                            }
                        }
                        Direction.DOWN -> {
                            when {
                                y + speed <= 416 && onMoveDown() -> y += speed
                                Dynamic.randomBranch() -> {
                                    fire()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                                moving && !City.pause -> {
                                    direction = Dynamic.randomWithoutDown()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                            }
                        }
                        Direction.LEFT -> {
                            when {
                                x - speed >= 48 && onMoveLeft() -> x -= speed
                                Dynamic.randomBranch() -> {
                                    fire()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                                moving && !City.pause -> {
                                    direction = Dynamic.randomWithoutLeft()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                            }
                        }
                        Direction.RIGHT -> {
                            when {
                                x + speed <= 432 && onMoveRight() -> x += speed
                                Dynamic.randomBranch() -> {
                                    fire()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                                moving && !City.pause -> {
                                    direction = Dynamic.randomWithoutRight()
                                    Thread.sleep(100L)
                                    break@loop
                                }
                            }
                        }
                    }
                    Thread.sleep(35L)
                }

                if (alive && !City.pause && City.gainFocus && City.power.clockTime == 0) {
                    if (i == 1) {
                        fire()
                    } else if (!City.pause) {
                        direction = Dynamic.randomDirection()
                    }
                }
            }
        }
    }

    override fun run() {
        // 当敌方坦克存活时
        while (alive && Animator.countdown[1] > 0) {
            if (!City.pause && moving) {
                // 射击玩家坦克
                shootPlayer()

                // 坦克自主移动
                move()

                // 射击基地
                shootBase()
            } else {
                Thread.sleep(100L)
            }
        }
    }

    companion object {
        // 创建坦克生成器
        var spawn = Spawn(240, 32)
    }
}