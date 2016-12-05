package cn.izouxiang.likeviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.izouxiang.likeview.CommentPathAdapter;
import cn.izouxiang.likeview.LikeView;
import cn.izouxiang.likeview.R;

/**
 * Created by zouxiang on 2016/12/1.
 */

public class SSAdapter extends RecyclerView.Adapter<SSAdapter.SSHolder> {

    private List<SSEntity> ssEntities;
    private Context mContext;

    public SSAdapter(@NonNull List<SSEntity> ssEntities, @NonNull Context mContext) {
        this.ssEntities = ssEntities;
        this.mContext = mContext;
    }

    @Override
    public SSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item, parent, false);
        return new SSHolder(view);
    }

    @Override
    public void onBindViewHolder(final SSHolder holder, int position) {
        final SSEntity entity = ssEntities.get(position);
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
        holder.name.setText(entity.name);
        holder.content.setText(entity.content);
    }

    @Override
    public int getItemCount() {
        if (null != ssEntities) {
            return ssEntities.size();
        }
        return 0;
    }

    class SSHolder extends RecyclerView.ViewHolder {
        LikeView like;
        LikeView comment;
        ImageView icon;
        TextView name;
        TextView content;

        SSHolder(View itemView) {
            super(itemView);
            like = (LikeView) itemView.findViewById(R.id.lv);
            comment = (LikeView) itemView.findViewById(R.id.comment);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
