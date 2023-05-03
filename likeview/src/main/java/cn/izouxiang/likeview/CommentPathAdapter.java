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
