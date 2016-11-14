package com.xuqi.propertyanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/12.
 */
public class MyAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "MyAnimationView";
    //定义小球大小的常量
    static final float BALL_SIZE = 50F;
    //定义小球从屏幕上方下落到屏幕底端的时间
    static final float FULL_TIME = 1000;
    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    public MyAnimationView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    public boolean onTouchEvent(MotionEvent event){
        //如果触碰事件不是按下，移动事件
        if(event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_MOVE){
            return false;
        }


        //如果是按下或者移动事件
        //事件发生处添加一个小球
        ShapeHolder newBall = addBall(event.getX(),event.getY());
        //记录小球落下时x,y坐标
        float startX = event.getX();
        float startY = event.getY();
        //记录小球最终的落点的y坐标
        float endY = getHeight() - BALL_SIZE;
        //获取屏幕高度
        float h = (float)getHeight();
        float eventY = event.getY();
        //计算动画持续时间
        int duration = (int) (FULL_TIME * ((h-eventY)/h));
        //定义小球“落下”的动画
        //让newBall的对象的y属性从事件发生点变化到屏幕下方
        ValueAnimator fallAnim = ObjectAnimator.ofFloat(newBall,"y",startY,endY);
        //设置动画的持续时间
        fallAnim.setDuration(duration);
        //设置fallAnim动画的插值方式，加速插值
        fallAnim.setInterpolator(new AccelerateInterpolator());
        //为fallAnim动画添加监听器
        //当valueAnimator的属性值发生改变时，将会激发该监听器的事件监听方法
        fallAnim.addUpdateListener(this);
        //定义将newBall的alpha属性执行从1到0的动画（即定义渐隐动画）
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(newBall,"alpha",1f,0f);
        //设置动画持续时间
        fadeAnim.setDuration(250);
        //为fadeAnim动画添加监听器
        fadeAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart: "+"fadeAnim start~~");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束时将动画关联的ShapeHolder删除
                balls.remove(((ObjectAnimator) animation).getTarget());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //为fadeAnim动画添加监听器
        //当ValueAnimator的属性值发生改变时，将会激发该监听器的事件监听方法
        fadeAnim.addUpdateListener(this);
        //定义一个AnimatorSet来组合动画
        AnimatorSet animatorSet = new AnimatorSet();
        //指定在播放fadeAnim之前，先播放fallAnim
        animatorSet.play(fallAnim).before(fadeAnim);
        //开始播放
        animatorSet.start();
        return true;
    }

    private ShapeHolder addBall(float x, float y) {
        //创建一个椭圆
        OvalShape circle = new OvalShape();
        //设置椭圆的宽高
        circle.resize(BALL_SIZE,BALL_SIZE);
        //设置该椭圆包装成Drawable对象
        ShapeDrawable drawable = new ShapeDrawable(circle);
        //创建一个ShapeHolder对象
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        //设置ShapeHolder的x,y坐标
        shapeHolder.setX(x - BALL_SIZE/2);
        shapeHolder.setY(y-BALL_SIZE/2);

        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);
        //将red，green，blue三个随机数除以4得到的商值组合成ARGB颜色
        int color = 0xff000000 + red << 16 | green<<8 | blue;//ff000000是16进制数，<<是右移运算，透明度ff表示完全不透明
        //获取drawable上关联的paint画笔
        Paint paint = drawable.getPaint();
        //将red，green，blue三个随机数除得到的商值组合成ARGB颜色
        int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
        //创建圆形渐变
        RadialGradient gradient = new RadialGradient(37.5f, 12.5f, BALL_SIZE, color, darkColor, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        //为shapeHolder设置paint画笔
        shapeHolder.setPaint(paint);
        balls.add(shapeHolder);

        return shapeHolder;
    }

    protected void onDraw(Canvas canvas){
        //遍历balls集合中的每个ShapeHolder对象
        for(ShapeHolder shapeHolder : balls){
            //保存canvas的当前坐标系统
            canvas.save();
            //坐标变换：将画布坐标系统平移到shapeHolder的x,y坐标处
            canvas.translate(shapeHolder.getX(),shapeHolder.getY());
            //将shapeHolder持有的圆形绘制在Canvas上
            shapeHolder.getShape().draw(canvas);
            //恢复Canvas坐标系统
            canvas.restore();
        }
    }
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //指定重绘该界面
        this.invalidate();
    }
}
