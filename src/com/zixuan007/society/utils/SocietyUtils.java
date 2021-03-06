package com.zixuan007.society.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.zixuan007.society.SocietyPlugin;
import com.zixuan007.society.domain.Society;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.zixuan007.society.utils.PluginUtils.SOCIETYFOLDER;
import static com.zixuan007.society.utils.PluginUtils.formatText;

/**
 * 公会插件工具类
 */
public class SocietyUtils {
    public static HashMap<String, ArrayList<Object>> onCreatePlayer = new HashMap<>();
    public static ArrayList<Society> societies = new ArrayList<>();
    /**
     * 指定的公会名称是否存在
     * @param societyName 公会名
     * @return
     */
    public static Boolean isSocietyNameExist(String societyName) {
        String filePath = SOCIETYFOLDER + societyName + ".yml";
        File societyFile = new File(filePath);
        return Boolean.valueOf(societyFile.exists());
    }

    /**
     * 获取当前格式化后的日期
     * @return
     */
    public static String getFormatDateTime() {
        long nowTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(Long.valueOf(nowTime));
    }

    /**
     * 指定的玩家是否加入过公会
     * @param playerName 玩家名
     * @return
     */
    public static boolean isJoinSociety(String playerName) {
        ArrayList<Society> societies = SocietyUtils.societies;
        for (Society society : societies) {
            for (Map.Entry<String, ArrayList<Object>> entry : (Iterable<Map.Entry<String, ArrayList<Object>>>)society.getPost().entrySet()) {
                String name = entry.getKey();
                if (name.equals(playerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param playerName
     * @return
     */
    public static Society getSocietyByPlayerName(String playerName) {
        ArrayList<Society> societies = SocietyUtils.societies;
        for (Society society : societies) {
            for (Map.Entry<String, ArrayList<Object>> entry : (Iterable<Map.Entry<String, ArrayList<Object>>>)society.getPost().entrySet()) {
                String name = entry.getKey();
                if (name.equals(playerName)) {
                    return society;
                }
            }
        }
        return null;
    }

    public static List<String> getMemberList(Society society, int currentPage) {
        return getMemberList(society, currentPage, 10);
    }

    /**
     * 获取当前公会的成员列表
     * @param society 公会实体类
     * @param currentPage 当前页数
     * @return
     */
    public static List<String> getMemberList(Society society, int currentPage, int limit) {
        HashMap<String, ArrayList<Object>> postMap = society.getPost();
        ArrayList<HashMap<String, Object>> postList = new ArrayList<>();
        ArrayList<String> tempList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Object>> entry : postMap.entrySet()) {
            ArrayList<Object> value = entry.getValue();
            final String playerName = entry.getKey();
            final Integer grade = (Integer)value.get(1);
            postList.add(new HashMap<String, Object>() {
                {
                    put("name",playerName);
                    put("grade",grade);
                }
            });
        }


        Collections.sort(postList, new Comparator<HashMap<String, Object>>() {
            public int compare(HashMap<String, Object> map1, HashMap<String, Object> map2) {
                Integer grade = (Integer)map1.get("grade");
                Integer grade1 = (Integer)map2.get("grade");
                return (grade.intValue() < grade1.intValue()) ? 1 : ((grade.intValue() > grade1.intValue()) ? -1 : (grade.equals(grade1) ? 0 : -1));
            }
        });
        postList.forEach(map -> {
            String name = (String)map.get("name");
            tempList.add(name);
        });
        ArrayList<String> members = tempList;
        int pageNumber = (members.size() % limit == 0) ? (society.getPost().size() / limit) : (society.getPost().size() / limit + 1);
        if (currentPage > pageNumber) return null;
        if (currentPage == 1) {
            if (members.size() <= limit) return members;
            if (members.size() > limit) return members.subList(0, limit);
        } else {
            int pageNumberSize = --currentPage * limit;
            List<String> subMembers = members.subList(pageNumberSize, members.size());
            if (subMembers.size() < limit) {
                return members.subList(pageNumberSize, pageNumberSize + subMembers.size());
            }
            return members.subList(pageNumberSize, pageNumberSize + limit);
        }

        return null;
    }

    /**
     * 检测字符串内容是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public static int getTotalMemberPage(Society society) {
        return getTotalMemberPage(society, 10);
    }

    /**
     * 获取指定公会成员列表总页数
     * @param society
     * @return
     */
    public static int getTotalMemberPage(Society society, int limit) {
        return (society.getPost().size() % limit == 0) ? (society.getPost().size() / limit) : (society.getPost().size() / limit + 1);
    }

    /**
     * 获取公会列表
     * @param currentPage 当前页面
     * @return
     */
    public static List<Society> getSocietyList(int currentPage) {
        ArrayList<Society> societies = SocietyUtils.societies;
        int totalPage = (societies.size() % 10 == 0) ? (societies.size() / 10) : (societies.size() / 10 + 1);
        if (currentPage > totalPage) return null;
        if (currentPage == 1) {
            if (societies.size() <= 10) return societies;
            if (societies.size() > 10) return societies.subList(0, 10);
        } else {
            int pageNumberSize = --currentPage * 10;
            List<Society> subMembers = societies.subList(pageNumberSize, societies.size());
            if (subMembers.size() < 10) {
                return societies.subList(pageNumberSize, pageNumberSize + subMembers.size());
            }
            return societies.subList(pageNumberSize, pageNumberSize + 10);
        }

        return null;
    }

    public static int getTotalSocietiesPage() {
        return (SocietyUtils.societies.size() % 10 == 0) ? (SocietyUtils.societies.size() / 10) : (SocietyUtils.societies.size() / 10 + 1);
    }

    /**
     * 获获取公会列表总页数
     * @param currentPage
     * @param limit
     * @return
     */
    public static int getSocietyListTotalPage(int currentPage, int limit) {
        ArrayList<Society> societies = SocietyUtils.societies;
        int totalPage = (societies.size() % limit == 0) ? (societies.size() / limit) : (societies.size() / limit + 1);
        return totalPage;
    }

    /**
     * 获取公会实体类通过公会ID
     * @param sid 公会ID
     * @return
     */
    public static Society getSocietysByID(long sid) {
        for (Society society : SocietyUtils.societies) {
            if (society.getSid() == sid) {
                return society;
            }
        }
        return null;
    }

    /**
     * 获取玩家所在公会的职位
     * @param playerName
     * @return 没有则返回-1
     */
    public static int getPostGradeByName(String playerName) {
        Config config = SocietyPlugin.getInstance().getConfig();
        ArrayList<HashMap<String, Object>> post = (ArrayList<HashMap<String, Object>>)config.get("post");
        for (HashMap<String, Object> map : post) {
            Integer grade = (Integer)map.get("grade");
            String name1 = (String)map.get("name");
            if (name1.equals(playerName))
                return grade.intValue();
        }
        return -1;
    }

    /**
     * 检测玩家是否是会长
     * @param playerName 玩家名称
     * @return
     */
    public static boolean isChairman(String playerName) {
        for (Society society : SocietyUtils.societies) {
            if (society.getPresidentName().equals(playerName)) return true;
        }
        return false;
    }

    /**
     * 移除公会
     * @param societyName 公会名称
     */
    public static void removeSociety(String societyName) {
        String path = SOCIETYFOLDER + societyName + ".yml";
        File file = new File(path);
        System.gc();
        boolean isdelete = file.delete();
        if (isdelete) {
            SocietyPlugin.getInstance().getLogger().info("§a公会 §b" + file.getName() + " §a删除成功");
        } else {
            SocietyPlugin.getInstance().getLogger().info("§c公会 §4" + file.getName() + " §c删除失败");
        }
    }

    /**
     * 获取当前玩家的职位
     * @param playerName 玩家名称
     * @param society 玩家所在的公会
     * @return
     */
    public static String getPostByName(String playerName, Society society) {
        if(society == null) return "无职位";
        ArrayList<Object> list = society.getPost().get(playerName);
        if(list.size() < 1) return "无职位";
        return (String)list.get(0);
    }

    /**
     * 格式化按钮文本
     * @param tipText
     * @param player
     * @return
     */
    public static String formatButtomText(String tipText, Player player) {
        return formatText(tipText, player);
    }

    /**
     * 格式化聊天文本
     * @param player
     * @param message
     * @return
     */
    public static String formatChat(Player player, String message) {
        Config config = SocietyPlugin.getInstance().getConfig();
        String chatText = (String)config.get("chatFormat");
        chatText = chatText.replaceAll("\\$\\{message\\}", message);
        return formatText(chatText, player);
    }


    /**
     * 获取创建下一个公会的ID
     * @return
     */
    public static long getNextSid() {
        ArrayList<Society> societies = SocietyUtils.societies;
        int size = SocietyUtils.societies.size();
        if (size == 0) return 1L;
        long max = 0L;
        for (Society society : societies) {
            if (society.getSid() > max) {
                max = society.getSid();
            }
        }
        return ++max;
    }

    /**
     * 获取当前配置信息的所有职位
     * @return
     */
    public static List<String> getAllPost() {
        List<Map<String, Object>> post = (List<Map<String, Object>>)SocietyPlugin.getInstance().getConfig().get("post");
        ArrayList<String> arrayList = new ArrayList<>();
        for (Map<String, Object> map : post) {
            String name = (String)map.get("name");
            if (name.equals("会长"))
                continue;  arrayList.add(name);
        }
        return arrayList;
    }

    /**
     * 添加成员
     * @param playerNmae
     * @param society
     */
    public static void addMember(String playerNmae, Society society) {
        SocietyUtils.societies.forEach(society1 -> society1.getTempApply().remove(playerNmae));
        society.getPost().put(playerNmae, new ArrayList() {
            {
                add("精英");
                add(1);
            }
        });
        society.saveData();
    }

    /**
     * 给公会所有成员发送标题信息
     * @param title
     */
    public static void sendMemberTitle(String title,Society society){
        if (society.getPost().size() <= 0) return;
        for (Map.Entry<String, ArrayList<Object>> entry : society.getPost().entrySet()) {
            String playerName = entry.getKey();
            if(!PluginUtils.isOnlineByName(playerName)) continue;
            if(society.getPresidentName().equals(playerName)) continue;
            Server.getInstance().getPlayer(playerName).sendTitle(title);
        }
    }

    /**
     * 检测指定的方块坐标是否已经设置过商店
     * @param block
     * @return
     */
    public static boolean isSetShop(Block block){
        for (Map.Entry<String, Object> entry : SocietyPlugin.getInstance().getTitleShopConfig().getAll().entrySet()) {
            String key = entry.getKey();
            List<Object> value = (List<Object>) entry.getValue();
            int titleSignX = (int) value.get(0);
            int titleSignY = (int) value.get(1);
            int titleSignZ = (int) value.get(2);
            if(titleSignX == block.getFloorX() && titleSignY == block.getFloorY() && titleSignZ == block.getFloorZ()) return true;
        }
        for (Map.Entry<String, Object> entry : SocietyPlugin.getInstance().getSocietyShopConfig().getAll().entrySet()) {
            String key = entry.getKey();
            HashMap<String,Object> value = (HashMap<String, Object>) entry.getValue();
            int societySignX= (int) value.get("x");
            int societySignY= (int) value.get("y");
            int societySignZ= (int) value.get("x");

        }
        return false;
    }


    /**
     * 解散公会移除所有的本公会商店
     * @param society
     */
    public static void removeSocietyShopBySid(Society society){
        Config societyShopConfig = SocietyPlugin.getInstance().getSocietyShopConfig();
        for (Map.Entry<String, Object> entry : societyShopConfig.getAll().entrySet()) {
            String key = entry.getKey();
            HashMap<String,Object> value = (HashMap<String, Object>) entry.getValue();
            int sid = (int) value.get("sid");
            if(sid == society.getSid()){
                value.put("dissolve",true);
                societyShopConfig.set(key,value);
                societyShopConfig.save();
                //进行玩家商店内容的返还
                removeShopSign(key);
            }
        }
    }

    /**
     * 移除商店木牌
     * @param key
     */
    public static void removeShopSign(String key){
        Config societyShopConfig = SocietyPlugin.getInstance().getSocietyShopConfig();
        HashMap<String,Object> societyData = (HashMap<String, Object>) societyShopConfig.get(key);
        int x = (int) societyData.get("x");
        int y = (int) societyData.get("y");
        int z = (int) societyData.get("z");
        String levelName = (String) societyData.get("levelName");
        for (Level level : Server.getInstance().getLevels().values()) {
            Vector3 vector3 = new Vector3(x, y, z);
            Block block = level.getBlock(vector3);
            if(block != null && block.getLevel().getName().equals(levelName) && block instanceof BlockWallSign){
                block.onBreak(Item.get(0));
            }
        }
    }

    /**
     * 移除玩家指定公会创建过的商店
     * @param society
     */
    public static void removeCreateShop(Society society,String playerName){
        Config societyShopConfig = SocietyPlugin.getInstance().getSocietyShopConfig();
        for (Map.Entry<String, Object> entry : societyShopConfig.getAll().entrySet()) {
            String key = entry.getKey();
            HashMap<String,Object> value = (HashMap<String, Object>) entry.getValue();
            int sid = (int) value.get("sid");
            String creator = (String) value.get("creator");
            if(society.getSid() == sid && creator.equals(playerName)){
                value.put("dissolve",true);
                removeShopSign(key);
            }
        }
    }

    /**
     * 加载公会配置文件
     */
    public static void loadSocietyConfig() {
        File societyFolder = new File(PluginUtils.SOCIETYFOLDER);
        SocietyPlugin societyPlugin = SocietyPlugin.getInstance();
        if (!societyFolder.exists()) societyFolder.mkdirs();
        File[] files = societyFolder.listFiles();
        for (File file : files) {
            Config config = new Config(file);
            societyPlugin.getSocietyConfigList().add(config);
            if (file.getName().endsWith(".yml")) SocietyUtils.societies.add(Society.init(config));
        }
        SocietyPlugin.getInstance().getLogger().debug(SocietyUtils.societies.toString());
    }

}