package cn.izouxiang.likeview.util;

import android.graphics.Path;

/**
 * Created by zouxiang on 2016/11/22.
 */

public class PathUtils {
    /**
     * 在一个正方形中画一个爱心
     *
     * @param length 正方形边长
     * @return 包含Heart路径的Path
     */
    public static Path getHeartPath(int length, float scale) {
        if (length < 0 || scale < 0) {
            throw new IllegalArgumentException("参数异常");
        }
        float s = length / 242f * scale;
        float offset = length / 2f * (1 - scale);
        Path path = new Path();
        path.moveTo(121 * s + offset, 225 * s + offset);
        path.cubicTo(-96 * s + offset, 119 * s + offset, 40 * s + offset, -55 * s + offset, 121 * s + offset, 55 * s + offset);
        path.cubicTo(202 * s + offset, -55 * s + offset, 338 * s + offset, 119 * s + offset, 121 * s + offset, 225 * s + offset);
        return path;
    }

    public static Path getHeartPath(int length) {
        return getHeartPath(length, 1f);
    }

    public static void addHeart(Path path, int x, int y, int length, float scale) {
        Path temp = getHeartPath(length, scale);
        temp.offset(x, y);
        path.addPath(temp);
    }

    public static void addHeart(Path path, int x, int y, int length) {
        Path temp = getHeartPath(length);
        temp.offset(x, y);
        path.addPath(temp);
    }

}
