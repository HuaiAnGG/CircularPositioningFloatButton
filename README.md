# CircularPositioningFloatButton
基于 ConstraintLayout + CircularPositioning(圆形定位) 的 FloatingActionButton(浮动按钮) 点赞+编辑+返回顶部的弹窗效果

**[详细内容请前往博客查看](https://blog.csdn.net/CalledJoker/article/details/89306490)**

## 项目讲解
实战部分主要讲解一下 `ConstraintLayout` 的 `Circular positioning(圆形定位)`功能。

**1、什么是Circular positioning呢？**
之所以称之为圆形定位，它就是以目标控件为圆心，通过设置角度和半径确定我们当前控件的位置，如官方图：
![Circular Positioning](https://img-blog.csdnimg.cn/20190415093334636.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NhbGxlZEpva2Vy,size_16,color_FFFFFF,t_70)

**2、目标**

我们先来看一下效果：
![效果图](https://mmbiz.qpic.cn/mmbiz_gif/MOu2ZNAwZwNUY4FqNvI53jRxjglHEKZVGauvf3oHEP49vzG049w1E8pmmblnbfib0GUsStY9h8QxRZfEuYzwAew/640?wx_fmt=gif&tp=webp&wxfrom=5&wx_lazy=1)

**3、设置布局**

布局的xml文件比较长，内容其实很简单，主要是四个 `FloatingActionButton` 和三个 `Group`，`Group` 在的 `ConstraintLayout` 中用来统一的控制视图的显示和隐藏，如果只用一个 `Group` 并不能让我们的控件有序的显示和隐藏，而 `FloatingActionButton` 由于不能使用 `setVisibility` 方法，只能使用 `Group` 管理 `FloatingActionButton` 的显示和隐藏，因此使用三个 `Group` 来实现上图三个 `FloatingActionButton` 有序的显示和隐藏（本来打算使用 `FloatingActionButton` 代替 `ImageView` 减少工作量的， `FloatingActionButton`导致的问题反而使工作量增加了，哈哈～）, **`activity_main.xml`** 如下：
```xml
<android.support.constraint.ConstraintLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab_add"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="32dp"
        android:layout_marginEnd="32dp"
        android:backgroundTint="@color/colorAccent"
        android:padding="10dp"
        android:src="@drawable/ic_fb_add"
        app:fabSize="normal"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:pressedTranslationZ="20dp"
        app:rippleColor="#1f000000" />

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab_like"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="32dp"
        android:layout_marginEnd="32dp"
        android:visibility="gone"
        android:backgroundTint="@color/colorAccent"
        android:padding="10dp"
        android:src="@drawable/ic_fb_like"
        app:fabSize="normal"
        app:layout_constraintCircle="@+id/fab_add"
        app:layout_constraintCircleRadius="80dp"
        app:layout_constraintCircleAngle="270"
        app:pressedTranslationZ="20dp"
        app:rippleColor="#1f000000" />

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab_write"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="32dp"
        android:layout_marginEnd="32dp"
        android:backgroundTint="@color/colorAccent"
        android:padding="10dp"
        android:src="@drawable/ic_fb_write"
        app:fabSize="normal"
        app:layout_constraintCircle="@+id/fab_add"
        app:layout_constraintCircleRadius="80dp"
        app:layout_constraintCircleAngle="315"
        app:pressedTranslationZ="20dp"
        app:rippleColor="#1f000000" />

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab_top"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="32dp"
        android:layout_marginEnd="32dp"
        android:backgroundTint="@color/colorAccent"
        android:padding="10dp"
        android:src="@drawable/ic_fb_top"
        app:fabSize="normal"
        app:layout_constraintCircle="@+id/fab_add"
        app:layout_constraintCircleRadius="80dp"
        app:layout_constraintCircleAngle="360"
        app:pressedTranslationZ="20dp"
        app:rippleColor="#1f000000" />

    <android.support.constraint.Group
        android:id="@+id/gp_like"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="fab_like"/>

    <android.support.constraint.Group
        android:id="@+id/gp_write"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="fab_write"/>

    <android.support.constraint.Group
        android:id="@+id/gp_top"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="fab_top"/>

</android.support.constraint.ConstraintLayout>
```

**4、业务逻辑**

首先确定我们需要使用的实例：

```java
private FloatingActionButton mAdd;
private FloatingActionButton mLike;
private FloatingActionButton mWrite;
private FloatingActionButton mTop;
private Group likeGroup;
private Group writeGroup;
private Group topGroup;
// 动画集合，用来控制动画的有序播放
private AnimatorSet animatorSet;
// 圆的半径
private int radius;
// FloatingActionButton宽度和高度，宽高一样
private int width;
```

接着初始化我们的控件，这里的代码比较简单，`initListener()` 我们放在后面介绍：

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_constraint);

    initWidget();
    initListener();
}

@Override
protected void onResume() {
    super.onResume();

    // 动态获取FloatingActionButton的宽
    mAdd.post(new Runnable() {
        @Override
        public void run() {
            width = mAdd.getMeasuredWidth();
        }
    });
    // 在xml文件里设置的半径
    radius = UiUtils.dp2px(this, 80);
}

private void initWidget() {
    mAdd = findViewById(R.id.fab_add);
    mLike = findViewById(R.id.fab_like);
    mTop = findViewById(R.id.fab_top);
    mWrite = findViewById(R.id.fab_write);
    likeGroup = findViewById(R.id.gp_like);
    writeGroup = findViewById(R.id.gp_write);
    topGroup = findViewById(R.id.gp_top);
    // 将三个弹出的FloatingActionButton隐藏
    setViewVisible(false);
}

private void setViewVisible(boolean isShow) {
    likeGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
    writeGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
    topGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
}
```

我们的重点就在 `initListener()` 里面，思路就是利用属性动画控制 `ConstraintLayout.LayoutParams`，从而控制 `Circular positioning` 的角度和半径：
```java 
private void initListener() {
    mAdd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 播放动画的时候不可以点击
            if(animatorSet != null && animatorSet.isRunning())
                return;

            // 判断播放显示还是隐藏动画
            if(likeGroup.getVisibility() != View.VISIBLE) {
                animatorSet = new AnimatorSet();
                ValueAnimator likeAnimator = getValueAnimator(mLike, false, likeGroup,0);
                ValueAnimator writeAnimator = getValueAnimator(mWrite, false, writeGroup,45);
                ValueAnimator topAnimator = getValueAnimator(mTop, false, topGroup,90);
                animatorSet.playSequentially(likeAnimator, writeAnimator, topAnimator);
                animatorSet.start();
            }else {
                animatorSet = new AnimatorSet();
                ValueAnimator likeAnimator = getValueAnimator(mLike, true, likeGroup,0);
                ValueAnimator writeAnimator = getValueAnimator(mWrite, true, writeGroup,45);
                ValueAnimator topAnimator = getValueAnimator(mTop, true, topGroup,90);
                animatorSet.playSequentially(topAnimator, writeAnimator, likeAnimator);
                animatorSet.start();
            }

        }
    });
}

/**
 * 获取ValueAnimator
 * 
 * @param button FloatingActionButton
 * @param reverse 开始还是隐藏
 * @param group Group
 * @param angle angle 转动的角度
 * @return ValueAnimator
 */
private ValueAnimator getValueAnimator(final FloatingActionButton button, 
		final boolean reverse, final Group group, final int angle) {
    ValueAnimator animator;
    if (reverse)
        animator = ValueAnimator.ofFloat(1, 0);
    else
        animator = ValueAnimator.ofFloat(0, 1);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float v = (float) animation.getAnimatedValue();
            ConstraintLayout.LayoutParams params 
            	= (ConstraintLayout.LayoutParams) button.getLayoutParams();
            params.circleRadius = (int) (radius * v);
            //params.circleAngle = 270f + angle * v;
            params.width = (int) (width * v);
            params.height = (int) (width * v);
            button.setLayoutParams(params);
        }
    });
    animator.addListener(new SimpleAnimation() {
        @Override
        public void onAnimationStart(Animator animation) {
            group.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(group == likeGroup && reverse){
                setViewVisible(false);
            }
        }
    });
    animator.setDuration(300);
    animator.setInterpolator(new DecelerateInterpolator());
    return animator;
}

abstract class SimpleAnimation implements Animator.AnimatorListener{
    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
```

屏幕工具类
```java
/**
 * @description: 屏幕的工具类
 * @author: HuaiAngg
 * @create: 2019-04-15 8:49
 */
public class UiUtils {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources()
        	.getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources()
        	.getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}

```

这样写完效果就出来了：

![初步效果](https://mmbiz.qpic.cn/mmbiz_gif/MOu2ZNAwZwNUY4FqNvI53jRxjglHEKZVhPyZZwzeJ9icHib5picsibOuVhs77icOlXDIn4r7Uu8xxtiaEBemicbeK5GIA/640?wx_fmt=gif&tp=webp&wxfrom=5&wx_lazy=1)

如果你觉得弹出的曲线不够圆滑，你可以在 `getValueAnimator` 方法中取消对 `//params.circleAngle = 270f + angle * v;` 这行的注释，效果就如本章一开始的效果。

## 总结
本文的思路就是利用属性动画控制ConstraintLayout.LayoutParams，从而控制Circular positioning的角度和半径，内容比较简单，前提是你得掌握属性动画和ConstraintLayout的使用。本人水平有限，难免有误，如有错误，欢迎提出。
