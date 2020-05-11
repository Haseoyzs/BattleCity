package battlecity.view

import battlecity.movable.Player
import battlecity.movable.Tank
import battlecity.util.Animator
import battlecity.util.Audio
import battlecity.util.Config
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

// 游戏启动界面
internal class StartPanel(private val client: Client) : JPanel(), ActionListener {
    // 创建按钮
    private val buttons = arrayOf(
            JButton("1 Player"), JButton("2 Players"),
            JButton("Map Editor"), JButton("Setting"))

    // 绘制游戏启动界面背景图片
    private val cover = JLabel(ImageIcon(Animator.getImage("Cover")))

    // 执行初始化的相关操作
    init {
        // 不使用任何布局方式
        layout = null

        buttons.forEach {
            // 设置按钮字体
            it.font = client.font16
            // 设置按钮默认不获取焦点
            it.isFocusPainted = false
            // 监听按钮的点击事件
            it.addActionListener(this)
            // 将按钮添加到开始面板
            add(it)
        }

        // 设置每个按钮的位置和大小
        buttons[0].setBounds(33, 190, 135, 36)
        buttons[1].setBounds(33, 240, 135, 36)
        buttons[2].setBounds(33, 290, 135, 36)
        buttons[3].setBounds(33, 340, 135, 36)

        // 设置背景图片的位置和大小
        cover.setBounds(0, 0, 518, 500)
        // 将背景图片添加到开始面板
        add(cover)

        // 刷新开始面板
        validate()
    }

    // 执行游戏开始时的一些准备工作
    private fun onStart() {
        // 设置客户端的菜单条
        client.jMenuBar = client.menuBar
        // 移除开始面板
        client.remove(this)
        // 添加战场面板到客户端
        client.add(client.city)
        // 刷新客户端
        client.validate()
        // 客户端获取焦点
        client.requestFocus()

        // 当没有使用自定义地图时
        if (!client.useCustomMap) {
            // 加载默认地图
            City.stage.loadMap()
        }

        // 播放游戏开始音频
        Thread(Audio).start()
        // 启动游戏战场的线程
        Thread(client.city).start()

        // 监听玩家 1 按键
        client.addKeyListener(Player(Tank.Group.PLAYER1, 176))
    }

    // 监听按钮的点击事件
    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            // 按下 1 个玩家按钮时
            buttons[0] -> onStart()
            // 按下 2 个玩家按钮时
            buttons[1] -> {
                onStart()
                client.addKeyListener(Player(Tank.Group.PLAYER2, 304))
                Config.maxCount = 6
            }
            // 按下地图编辑器按钮时
            buttons[2] -> {
                client.mapEditor = MapEditor(client)
                client.remove(client.startPanel)
                client.add(client.mapEditor)
                client.validate()
                client.requestFocus()
                client.addKeyListener(client.mapEditor)
            }
            // 按下设置按钮时
            buttons[3] -> {
                client.setting = Setting(client)
                client.setting.isVisible = true
            }
        }
    }
}