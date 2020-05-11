package battlecity.movable

import battlecity.flash.*
import battlecity.stage.Grid
import battlecity.util.Audio
import battlecity.view.City

// 炮弹检测类
internal class Check {
    // 炮弹运动控制
    private fun move(shell: Shell) {
        // 当战场面板处于非暂停状态并且获得焦点、炮弹也存活时
        if (!City.pause && City.gainFocus && shell.alive) {
            // 当炮弹到达边界时
            if (shell.x !in 32..448 || shell.y !in 16..432) {
                // 将炮弹设为死亡状态
                shell.alive = false
                // 添加一个爆炸到爆炸集合中
                City.explosions.add(Explosion(shell.x, shell.y, -9))
                // 当炮弹是玩家坦克的炮弹时
                if (shell.group != Tank.Group.ENEMY) {
                    // 播放击中音频
                    Audio.play("HitObject")
                }

                return
            }
        }

        // 根据炮弹的位置改变炮弹的坐标
        when (shell.direction) {
            Tank.Direction.UP -> shell.y -= shell.speed
            Tank.Direction.DOWN -> shell.y += shell.speed
            Tank.Direction.LEFT -> shell.x -= shell.speed
            Tank.Direction.RIGHT -> shell.x += shell.speed
        }
    }

    // 检测炮弹是否互相碰撞
    private fun hitShell(shell: Shell) {
        if (shell.alive) {
            City.shells.forEach {
                if (shell.group != it.group && it.alive && shell.getRect().intersects(it.getRect())) {
                    it.alive = false
                    shell.alive = false
                }
            }
        }
    }

    // 检测炮弹是否击中墙
    private fun hitWall(shell: Shell) {
        // 检测炮弹所在位置的地图元素
        fun hitCheck(shell: Shell, row: Int, column: Int) {
            // 计算炮弹所在的坐标位置
            val i = when {
                row < 0 -> 0
                row > 25 -> 25
                else -> row
            }
            val j = when {
                column < 0 -> 0
                column > 25 -> 25
                else -> column
            }

            // 当被检测的地图元素是砖墙或者铁墙时
            if (City.stage.map[i][j].type == Grid.Type.BRICK_WALL || City.stage.map[i][j].type == Grid.Type.STEEL_WALL) {
                // 获取炮弹所在位置的地图元素实例
                var x = shell.x
                var y = shell.y
                val wall = City.stage.map[i][j]

                // 当炮弹是类型 4 的玩家坦克发出的炮弹或者墙不是完整的墙时
                if (shell.group != Tank.Group.ENEMY && shell.type == Tank.Type.TYPE4 || wall.part != Grid.Part.WHOLE) {
                    // 将炮弹所在位置的地图元素设为空格
                    wall.type = Grid.Type.BLANK

                    // 当墙是完整的墙时（铁墙）
                    if (wall.part == Grid.Part.WHOLE) {
                        when (shell.direction) {
                            Tank.Direction.UP, Tank.Direction.DOWN -> y = wall.y + 8
                            Tank.Direction.LEFT, Tank.Direction.RIGHT -> x = wall.x + 8
                        }
                    } else { // 当墙是砖墙时
                        when (shell.direction) {
                            Tank.Direction.UP -> y = wall.y + 4
                            Tank.Direction.DOWN -> y = wall.y + 12
                            Tank.Direction.LEFT -> x = wall.x + 4
                            Tank.Direction.RIGHT -> x = wall.x + 12
                        }
                    }
                } else if (wall.type != Grid.Type.STEEL_WALL) {
                    // 当墙是砖墙时
                    wall.part = when (shell.direction) {
                        Tank.Direction.UP -> {
                            y = wall.y + 12
                            Grid.Part.UP
                        }
                        Tank.Direction.DOWN -> {
                            y = wall.y + 4
                            Grid.Part.BOTTOM
                        }
                        Tank.Direction.LEFT -> {
                            x = wall.x + 12
                            Grid.Part.LEFT
                        }
                        Tank.Direction.RIGHT -> {
                            x = wall.x + 4
                            Grid.Part.RIGHT
                        }
                    }
                }

                // 当炮弹存活时
                if (shell.alive) {
                    // 将炮弹设为死亡状态
                    shell.alive = false
                    // 添加一个爆炸到爆炸集合中
                    City.explosions.add(Explosion(x, y, -9))

                    // 当炮弹是玩家坦克的炮弹时
                    if (shell.group != Tank.Group.ENEMY) {
                        // 当墙变为不完整或者墙消失了
                        if (wall.part != Grid.Part.WHOLE || wall.type == Grid.Type.BLANK) {
                            // 播放墙爆炸的音频
                            Audio.play("WallExplode")
                        } else {
                            // 播放击中音频
                            Audio.play("HitObject")
                        }
                    }
                }
            } else if (!City.destroy && City.stage.map[i][j].type == Grid.Type.BASE) { // 当炮弹击中基地时
                // 将炮弹设为死亡状态
                shell.alive = false
                // 将
                City.destroy = true
                City.explosions.add(Explosion(shell.x, shell.y, -9))
                City.explosions.add(Explosion(240, 416, 48))
                Audio.play("PlayerExplode")
            }
        }

        // 检测炮弹是否击中墙的上侧和底侧
        fun onUpDown(shell: Shell, x: Int, y: Int, delta: Int) {
            val row = y + delta shr 4
            val leftColumn = x - 3 shr 4
            val rightColumn = x + 3 shr 4

            hitCheck(shell, row, leftColumn)
            hitCheck(shell, row, rightColumn)
        }
        // 检测炮弹是否击中墙的左侧和右侧
        fun onLeftRight(shell: Shell, x: Int, y: Int, delta: Int) {
            val column = x + delta shr 4
            val topRow = y + 3 shr 4
            val bottomRow = y - 3 shr 4

            hitCheck(shell, topRow, column)
            hitCheck(shell, bottomRow, column)
        }

        if (shell.alive) {
            when (shell.direction) {
                Tank.Direction.UP -> onUpDown(shell, shell.x - 32, shell.y - 16, -shell.speed)
                Tank.Direction.DOWN -> onUpDown(shell, shell.x - 32, shell.y - 16, shell.speed)
                Tank.Direction.LEFT -> onLeftRight(shell, shell.x - 32, shell.y - 16, -shell.speed)
                Tank.Direction.RIGHT -> onLeftRight(shell, shell.x - 32, shell.y - 16, shell.speed)
            }
        }
    }

    // 根据玩家击中的敌方坦克类型统计玩家的得分
    private fun addScore(player: Player, ptsIndex: Int) {
        val score = (ptsIndex + 1) * 100
        ++player.points[ptsIndex]
        ++player.points[4]
        player.scores[ptsIndex] += score
        player.scoreTotal += score

        // 当玩家分数超过 20000 时
        if (player.canReward && player.scoreTotal >= 20000) {
            // 给玩家加一条命
            ++player.health
            // 播放加命的音频
            Audio.play("RaiseHealth")

            // 只加一次
            player.canReward = false
        }
    }
    // 检测玩家坦克的炮弹是否击中敌方坦克
    private fun hitEnemy(shell: Shell) {
        // 当炮弹存活并且是玩家坦克的炮弹时
        if (shell.group != Tank.Group.ENEMY) {
            City.enemies.forEach breaking@ {
                // 当玩家坦克的炮弹击中敌方坦克时
                if (shell.alive && it.alive && shell.getRect().intersects(it.getRect())) {
                    // 将炮弹设为死亡状态
                    shell.alive = false
                    // 添加一个爆炸到爆炸集合中
                    City.explosions.add(Explosion(shell.x, shell.y, -9))

                    // 当被击中的坦克是道具坦克时
                    if (it.flashing) {
                        // 取消道具坦克的标记
                        it.flashing = false
                        // 生成道具
                        City.power.powerSpawn()
                        // 播放道具生成音频
                        Audio.play("PowerUpSpawn")
                    }

                    // 当敌方坦克是类型 4 时
                    if (it.health > 0) {
                        // 削弱敌方坦克
                        --it.health
                        // 播放击中音频
                        Audio.play("HitArmor")
                        // 跳出循环
                        return@breaking
                    }

                    // 将敌方坦克设为死亡状态
                    it.alive = false
                    // 添加一个爆炸到爆炸集合中
                    City.explosions.add(Explosion(it.x, it.y, 48))
                    // 给玩家添加对应的得分
                    addScore(City.players[shell.group.ordinal], it.type.ordinal)
                    // 将分数添加到分数集合中
                    City.scores.add(Score(it.x, it.y, it.type.ordinal + 1))
                    // 播放敌方坦克爆炸音频
                    Audio.play("EnemyExplode")
                }
            }
        }
    }
    // 检测炮弹是否击中坦克
    private fun hitPlayer(shell: Shell) {
        if (shell.group == Tank.Group.ENEMY) {
            City.players.forEach {
                // 当玩家坦克存活时
                if (shell.alive && it.alive) {
                    // 当炮弹是敌方坦克的炮弹时
                    if (shell.group == Tank.Group.ENEMY) {
                        // 当炮弹击中玩家坦克时
                        if (shell.getRect().intersects(it.getRect())) {
                            // 将炮弹设为死亡状态
                            shell.alive = false

                            // 当玩家坦克的防护时间为0时
                            if (it.helmetTime == 0) {
                                // 添加爆炸到爆炸集合中
                                City.explosions.add(Explosion(shell.x, shell.y, -9))
                                City.explosions.add(Explosion(it.x, it.y, 48))

                                // 当玩家的命大于 0 时
                                if (it.health > 0) {
                                    // 减一条命
                                    --it.health
                                    // 执行玩家坦克被击中时的一些操作
                                    it.onHit()
                                } else {
                                    // 玩家坦克设为死亡状态
                                    it.alive = false
                                }
                                // 播放玩家坦克爆炸音频
                                Audio.play("PlayerExplode")
                            }
                        }
                    } else if (shell.group != it.group && shell.getRect().intersects(it.getRect())) {
                        shell.alive = false

                        if (it.helmetTime == 0) {
                            it.flashing = true
                            it.flashTime = 33
                            City.explosions.add(Explosion(shell.x, shell.y, -9))
                        }
                    }
                }
            }
        }
    }


    internal fun run() {
        while (true) {
            City.shells.forEach {
                // 移动炮弹
                move(it)

                // 检测炮弹碰撞
                hitShell(it)

                // 检测击中砖墙
                hitWall(it)

                // 检测击中敌方坦克
                hitEnemy(it)

                // 检测击中玩家坦克
                hitPlayer(it)
            }
            Thread.sleep(15L)
        }
    }
}