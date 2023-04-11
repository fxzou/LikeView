# 仿即刻APP点赞桃心的效果
## 2016-12-5更新
- 修改测量逻辑
- 添加了对齐方式
- 添加了一个评论图形的GraphAdapter
- 修改了已知BUG

## 先看效果图
![](img/likeview.gif)
## 使用方法
### 布局配置
```
<cn.izouxiang.likeview.LikeView
            android:id="@+id/lv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:number="99"
            />
```
> 注意,一般不需要直接指定宽高,内部会根据字体大小自动测量

### 代码配置
```java
		//点赞view的设置
		holder.like.setActivated(entity.isLike);
        holder.like.setNumber(entity.likeNum);
        holder.like.setCallback(new LikeView.SimpleCallback() {
            @Override
            public void activate(LikeView view) {
                Snackbar.make(view, "你觉得" + entity.name + "很赞!", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void deactivate(LikeView view) {
                Snackbar.make(view, "你取消了对" + entity.name + "的赞!", Snackbar.LENGTH_SHORT).show();
            }
        });
        //评论view的设置
        holder.comment.setNumber(entity.commentNum);
        //设置图形适配器
        holder.comment.setGraphAdapter(CommentPathAdapter.getInstance());
        holder.comment.setCallback(new LikeView.SimpleCallback(){
            @Override
            public boolean onClick(LikeView view) {
                Snackbar.make(view, "你点击" + entity.name + "的评论按钮", Snackbar.LENGTH_SHORT).show();
                //返回true代表拦截此次点击,不使用默认的点击事件
                return true;
            }
        });
```
### 自定义图形适配器
```java
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
```
## 自定义配置
```
<resources>
    <declare-styleable name="LikeView">
        <!--当前数值,默认0-->
        <attr name="number" format="integer"/>
        <!--数字颜色,默认#888888-->
        <attr name="textColor" format="color"/>
        <!--图形外边颜色,默认#888888-->
        <attr name="graphColor" format="color"/>
        <!--当前激活时图形颜色,默认#ca5f5f-->
        <attr name="animateColor" format="color"/>
        <!--字体大小,决定控件高度以及图形大小,默认14sp-->
        <attr name="textSize" format="dimension"/>
        <!--动画时间,默认300-->
        <attr name="animateDuration" format="integer"/>
        <!--图形与数字间的距离,默认3dp-->
        <attr name="distance" format="dimension"/>
        <!--图形与数字高度的比例,默认1.3-->
        <attr name="graphTextHeightRatio" format="float"/>
        <!--图形外边绘制宽度,默认3dp-->
        <attr name="graphStrokeWidth" format="dimension"/>
        <!--数字绘制宽度,默认2dp-->
        <attr name="textStrokeWidth" format="dimension"/>
        <!--是否激活,默认false-->
        <attr name="isActivated" format="boolean"/>
        <!--是否自动测量数字修改后的宽度改变,防止更改状态时控件宽度改变,默认开启-->
        <attr name="autoMeasureMaxWidth" format="boolean"/>
        <!--是否不允许取消点赞,默认false-->
        <attr name="notAllowedCancel" format="boolean"/>
        <!--对齐方式,前三种默认垂直居中-->
        <attr name="gravity" format="enum">
            <!--居中-->
            <enum name="center" value="1"/>
            <!--左对齐-->
            <enum name="left" value="2"/>
            <!--右对齐-->
            <enum name="right" value="3"/>
            <!--开始点-->
            <enum name="start" value="4"/>
        </attr>
    </declare-styleable>
</resources>
```
## 接口
```java
/**
     * 事件监听接口
     */
    public interface Callback {
        /**
         * 点击事件监听
         *
         * @param view 当前View
         * @return 返回true则代表不使用默认的点击事件
         */
        boolean onClick(LikeView view);

        /**
         * 变为激活状态
         *
         * @param view 当前View
         */
        void activate(LikeView view);

        /**
         * 变为不激活状态
         *
         * @param view 当前View
         */
        void deactivate(LikeView view);
    }
    
    /**
     * 获取图形Path接口
     */
    public interface GraphAdapter {
        /**
         * 获取图形的Path
         *
         * @param view   当前View
         * @param length 可绘制图形区域正方形的边长
         * @return 带有图形的Path
         */
        Path getGraphPath(LikeView view, int length);
    }

```
## 声明
此项目为练手项目,当中可能存在BUG,发现BUG请指出,谢谢
# License
```
Copyright 2016 zFxiang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
