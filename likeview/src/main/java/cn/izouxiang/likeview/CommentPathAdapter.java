package cn.izouxiang.likeview;

import android.graphics.Path;

/**
 * Created by zouxiang on 2016/12/5.
 */

public class CommentPathAdapter implements LikeView.GraphAdapter {
    private static CommentPathAdapter instance;
    private static final float xOffsetScale = 0.06f;
    private static final float yOffsetScale = 0.2f;
    //可用单例模式
    public static CommentPathAdapter getInstance() {
        synchronized (CommentPathAdapter.class) {
            if (null == instance) {
                instance = new CommentPathAdapter();
            }
        }
        return instance;
    }
    //这里绘制你想要的图形
    @Override
    public Path getGraphPath(LikeView view, int length) {
        Path path = new Path();
        int dx = (int) (length * xOffsetScale);
        int dy = (int) (length * yOffsetScale);
        int w = (int) (length * (1 - xOffsetScale * 2));
        int h = (int) (length * (1 - yOffsetScale * 2));
        path.moveTo(dx, dy);
        path.lineTo(dx + w, dy);
        path.lineTo(dx + w, dy + h);
        path.lineTo(dx + (w * 0.35f), dy + h);
        path.lineTo(dx + (w * 0.1f), dy + (h * 1.4f));
        path.lineTo(dx + (w * 0.1f), dy + h);
        path.lineTo(dx, dy + h);
        path.lineTo(dx, dy);
        return path;
    }
}
// 添加时间备注 2023-04-29
// 添加时间备注 2023-04-30
// 添加时间备注 2023-05-01
// 添加时间备注 2023-05-02
// 添加时间备注 2023-05-03
// 添加时间备注 2023-05-04
// 添加时间备注 2023-05-05
// 添加时间备注 2023-05-06
// 添加时间备注 2023-05-07
// 添加时间备注 2023-05-08
// 添加时间备注 2023-05-09
// 添加时间备注 2023-05-10
// 添加时间备注 2023-05-11
// 添加时间备注 2023-05-12
// 添加时间备注 2023-05-13
// 添加时间备注 2023-05-14
// 添加时间备注 2023-05-15
// 添加时间备注 2023-05-16
// 添加时间备注 2023-05-17
// 添加时间备注 2023-05-18
// 添加时间备注 2023-05-19
// 添加时间备注 2023-05-20
// 添加时间备注 2023-05-21
// 添加时间备注 2023-05-22
// 添加时间备注 2023-05-23
// 添加时间备注 2023-05-24
// 添加时间备注 2023-05-25
// 添加时间备注 2023-05-26
// 添加时间备注 2023-05-27
// 添加时间备注 2023-05-28
// 添加时间备注 2023-05-29
// 添加时间备注 2023-05-30
// 添加时间备注 2023-05-31
// 添加时间备注 2023-06-01
// 添加时间备注 2023-06-02
// 添加时间备注 2023-06-03
// 添加时间备注 2023-06-04
// 添加时间备注 2023-06-05
// 添加时间备注 2023-06-06
