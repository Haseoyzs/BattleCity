package battlecity.view

import battlecity.movable.Check
import battlecity.util.*
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel
import javax.swing.*
import java.awt.*
import java.awt.event.*
import java.util.*
import kotlin.system.exitProcess

// 游戏客户端类
internal class Client : JFrame(), ActionListener {
    // 创建 16 号的界面字体
    internal val font16 = Font.createFont(Font.TRUETYPE_FONT, Client::class.java.getResourceAsStream("/Fonts/UI.ttf")).deriveFont(16f)
    // 创建 14 号的界面字体
    internal val font14 = font16.deriveFont(14f)
    // 获取屏幕的半宽
    internal val screenHalfW = Toolkit.getDefaultToolkit().screenSize.width shr 1
    // 获取屏幕的半高
    internal val screenHalfH = Toolkit.getDefaultToolkit().screenSize.height shr 1
    // 创建战场面板
    internal val city = City()
    // 创建开始面板
    internal var startPanel = StartPanel(this)
    // 定义设置面板
    internal lateinit var setting: Setting
    // 定义地图编辑面板
    internal lateinit var mapEditor: MapEditor

    // 创建菜单条
    internal val menuBar = JMenuBar()
    // 创建菜单
    private val menu = JMenu("Game(G)")
    // 创建菜单项
    private val menuItems = arrayOf(
        JMenuItem("Restart"), JMenuItem("Pause"),
        JMenuItem("Resume"), JMenuItem("Exit"))

    // 用于标记是否使用自定义地图
    internal var useCustomMap = false

    // 执行初始化的相关操作
    init {
        // 添加开始面板到窗口
        add(startPanel)
        // 监听战场面板的焦点变化
        addFocusListener(city)

        // 将菜单设置为默认不可选
        menuItems[2].isEnabled = false

        menuItems.forEach {
            // 设置菜单项的字体
            it.font = font14
            // 监听菜单项的点击事件
            it.addActionListener(this)
            // 将菜单项添加到菜单
            menu.add(it)
        }

        // 设置菜单和菜单项的快捷键
        menu.mnemonic = KeyEvent.VK_G
        menuItems[0].accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R,0)
        menuItems[1].accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P,0)
        menuItems[2].accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P,0)
        menuItems[3].accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0)

        // 将菜单添加到菜单条中
        menuBar.add(menu)

        // 设置游戏客户端标题
        title = "Battle City"
        // 将游戏客户端设置为不可调整大小
        isResizable = false
        enableInputMethods(false)
        // 设置游戏客户端关闭时的操作
        defaultCloseOperation = EXIT_ON_CLOSE
        // 设置游戏客户端在屏幕中的默认显示位置和大小
        setBounds(screenHalfW - 259, screenHalfH - 250, 518, 500)
        // 验证游戏客户端及其所有子组件
        validate()
        // 将游戏客户端设置为默认可见
        isVisible = true
    }

    // 该方法处理按钮的点击事件
    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            // 点击重新开始按钮时
            menuItems[0] -> {
                // 将暂停按钮设为可点击
                menuItems[1].isEnabled = true
                // 将继续按钮设为不可点击
                menuItems[2].isEnabled = false
                // 保存玩家当前总分
                city.saveRecord()
                // 重置战场
                city.onReset()
                // 播放音频
                Audio.start = true
                // 加载地图
                City.stage.loadMap()
                // 重置玩家
                City.players.forEach { it.onReset() }
                // 重置 GameOver 文字的显示位置
                Animator.gameOverXY = intArrayOf(-13, 429, 514)
            }
            // 点击暂停按钮时
            menuItems[1] -> {
                // 当 GameOver 文字的 Y 坐标大于 418 时暂停按钮有效
                if (Animator.gameOverXY[2] > 418) {
                    // 将暂停按钮设为不可点击
                    menuItems[1].isEnabled = false
                    // 将继续按钮设为可点击
                    menuItems[2].isEnabled = true

                    // 暂停敌方坦克
                    City.enemies.forEach { it.letStay() }
                    // 暂停炮弹
                    City.shells.forEach { it.letStay() }
                    // 将战场标记为暂停状态
                    City.pause = true
                    // 播放暂停音频
                    Audio.play("Pause")
                }
            }
            // 点击继续按钮时
            menuItems[2] -> {
                // 当 GameOver 文字的 Y 坐标大于 418 时继续按钮才有效
                if (Animator.gameOverXY[2] > 418) {
                    menuItems[1].isEnabled = true
                    menuItems[2].isEnabled = false

                    City.enemies.forEach { it.letMove() }
                    City.shells.forEach { it.letMove() }

                    City.pause = false
                }
            }
            // 点击退出按钮时
            menuItems[3] -> exitProcess(0)
        }
    }

    companion object {
        @JvmStatic
        // 主函数
        fun main(args: Array<String>) {
            // 设置窗口标题
            val props = Properties()
            props.setProperty("logoString", "Battle City")
            AluminiumLookAndFeel.setCurrentTheme(props)
            UIManager.setLookAndFeel(AluminiumLookAndFeel())

            // 启动游戏客户端
            Client()

            // 运行检测函数
            Check().run()
        }
    }
}



