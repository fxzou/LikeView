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

// 添加时间备注 2023-06-13
// 添加时间备注 2023-06-14
// 添加时间备注 2023-06-15
// 添加时间备注 2023-06-16
// 添加时间备注 2023-06-17
// 添加时间备注 2023-06-18
// 添加时间备注 2023-06-19
// 添加时间备注 2023-06-20
// 添加时间备注 2023-06-21
// 添加时间备注 2023-06-22
// 添加时间备注 2023-06-23
// 添加时间备注 2023-06-24
// 添加时间备注 2023-06-25
// 添加时间备注 2023-06-26
// 添加时间备注 2023-06-27
// 添加时间备注 2023-06-28
// 添加时间备注 2023-06-29
// 添加时间备注 2023-06-30
// 添加时间备注 2023-07-01
// 添加时间备注 2023-07-02
// 添加时间备注 2023-07-03
// 添加时间备注 2023-07-04
// 添加时间备注 2023-07-05
// 添加时间备注 2023-07-06
// 添加时间备注 2023-07-07
// 添加时间备注 2023-07-08
// 添加时间备注 2023-07-09
// 添加时间备注 2023-07-10
// 添加时间备注 2023-07-11
// 添加时间备注 2023-07-12
// 添加时间备注 2023-07-13
// 添加时间备注 2023-07-14
// 添加时间备注 2023-07-15
// 添加时间备注 2023-07-16
// 添加时间备注 2023-07-17
// 添加时间备注 2023-07-18
// 添加时间备注 2023-07-19
// 添加时间备注 2023-07-20
// 添加时间备注 2023-07-22
// 添加时间备注 2023-07-23
// 添加时间备注 2023-07-24
// 添加时间备注 2023-07-25
// 添加时间备注 2023-07-26
// 添加时间备注 2023-07-27
// 添加时间备注 2023-07-28
// 添加时间备注 2023-07-29
// 添加时间备注 2023-07-30
// 添加时间备注 2023-07-31
// 添加时间备注 2023-08-01
// 添加时间备注 2023-08-02
// 添加时间备注 2023-08-03
// 添加时间备注 2023-08-04
// 添加时间备注 2023-08-05
// 添加时间备注 2023-08-06
// 添加时间备注 2023-08-07
// 添加时间备注 2023-08-08
// 添加时间备注 2023-08-09
// 添加时间备注 2023-08-10
// 添加时间备注 2023-08-11
// 添加时间备注 2023-08-12
// 添加时间备注 2023-08-13
// 添加时间备注 2023-08-14
// 添加时间备注 2023-08-15
