package top.mrxiaom.sweet.drops;

import top.mrxiaom.pluginbase.func.language.IHolderAccessor;
import top.mrxiaom.pluginbase.func.language.Language;
import top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder;

import java.util.List;

import static top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder.wrap;

@Language(prefix = "messages.")
public enum Messages implements IHolderAccessor {
    commands__reload("&a配置文件已重载"),
    commands__debug__on("&f调试模式已&a开启"),
    commands__debug__off("&f调试模式已&c关闭"),
    commands__prefeb__notfound("&e找不到预制体 &b%s"),
    commands__prefeb__no_amount("&e请输入正确的数量"),
    commands__prefeb__given("&e已给予玩家&b %player% &e预制体&b %prefeb% &e共&b %count% &e个"),
    commands__help(
            "&d&lSweetDrops&r &e帮助命令",
            "&f/sd debug &e开启或关闭调试模式",
            "&f/sd prefeb <预制体ID> [数量] [玩家] &e给予某人指定数量的预制体",
            "&f/sd reload &e重载配置文件"
    ),
    player__notfound("&e玩家 &b%s &e不在线"),
    player__only("&e该命令只能由玩家执行"),

    ;
    Messages(String defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Messages(String... defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Messages(List<String> defaultValue) {
        holder = wrap(this, defaultValue);
    }
    private final LanguageEnumAutoHolder<Messages> holder;
    public LanguageEnumAutoHolder<Messages> holder() {
        return holder;
    }
}
