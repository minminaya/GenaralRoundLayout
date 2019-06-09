## GeneralRoundLayout

Android通用圆角布局，可以解决Android P版本xfermode方案裁剪黑边问题和xfermode在列表view中使用滑动时EGL内存泄露问题

---

### 其诞生有3个原因

- 1、之前使用的XferMode裁剪方案在P版本失效
- 2、xfermode圆角裁剪方案在RecyclerView中使用，滑动时会出现EGL内存泄露问题（系统api未做好内存回收），使用GeneralRound，可以解决L版本上的机器
- 3、希望可以快速将一个View装饰包装变成支持裁剪圆角的View

---

### GETTING START

###### 导入GeneralRoundLayout依赖

- 1、在Project 的build.gradle中

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```

- 2、在对应module中添加dependency

```

dependencies {
	        implementation 'com.github.minminaya:GenaralRoundLayout:1.0.0'
	}

```

- 3、在你想做裁剪的布局外层包裹

```

 <com.minminaya.widget.GeneralRoundFrameLayout
            android:layout_width="200dp"
            android:layout_height="200dp"
            android:layout_gravity="center"
            app:corner_radius="30dp">

            <View
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@color/colorAccent" />
        </com.minminaya.widget.GeneralRoundFrameLayout>
        
```

### 给自定义view加上圆角裁剪特性

GeneralRoundLayout设计初期是为了方便各种布局的扩展，因此可以使任何一个view支持圆角特性，你只需要重写几个方法

- 1、让你的自定义view比如GeneralRoundImageView实现IRoundView接口

```

interface IRoundView {
    fun setCornerRadius(cornerRadius: Float)
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
}

```

- 2、定义attrs属性

在你的attrs的文件中，定义declare-styleable属性（为了可以在xml文件中输入的时候自动提示）

```

<declare-styleable name="GeneralRoundImageView">
        <attr name="corner_radius" />
    </declare-styleable>
    
```

- 2、让GeneralRoundImageView实现IRoundView接口的方法

```

public class GeneralRoundImageView extends AppCompatImageView implements IRoundView {

    public GeneralRoundImageView(Context context) {
        this(context, null);
    }

    public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCornerRadius(float cornerRadius) {
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

}

```

- 3、在GeneralRoundImageView中定义GeneralRoundViewImpl对象，本质上是裁剪view的helper类，让其初始化，并将view的实现分发到GeneralRoundViewImpl


```

public class GeneralRoundImageView extends AppCompatImageView implements IRoundView {
    private GeneralRoundViewImpl generalRoundViewImpl;

    public GeneralRoundImageView(Context context) {
        this(context, null);
    }

    public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(this, context, attrs);
    }


    public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(this, context, attrs);
    }

    @Override
    public void setCornerRadius(float cornerRadius) {
        generalRoundViewImpl.setCornerRadius(cornerRadius);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        generalRoundViewImpl.onLayout(changed, left, top, right, bottom);
    }

    private void init(GeneralRoundImageView view, Context context, AttributeSet attrs) {
            generalRoundViewImpl = new GeneralRoundViewImpl(view,
                    context,
                    attrs,
                    R.styleable.GeneralRoundImageView,
                    R.styleable.GeneralRoundImageView_corner_radius);
    }

}

```

- 4、重写dispatchDraw方法，将实现类的方法包装super

```
    @Override
    protected void dispatchDraw(Canvas canvas) {
        generalRoundViewImpl.beforeDispatchDraw(canvas);
        super.dispatchDraw(canvas);
        generalRoundViewImpl.afterDispatchDraw(canvas);
    }

```

- 5、在你要使用的地方

```
 <com.minminaya.genaral.custom.GeneralRoundImageView
            android:layout_width="200dp"
            android:layout_height="200dp"
            android:layout_gravity="center"
            android:layout_marginTop="20dp"
            android:src="@color/colorPrimaryDark"
            app:corner_radius="60dp" />

```

- 6、done

### 如何同时解决xfermode内存泄露和Android P圆角失效问题

- 1、P版本圆角失效问题，具体可见GcsSloop大神的[rclayout](https://github.com/GcsSloop/rclayout)，有给出为何失效和解决的方案
- 2、由于xfermode方案会导致内存泄露，所以这里GeneralRoundLayout在L版本及以上不在使用其进行绘制，转而使用ViewOutlineProvider去进行圆角裁剪，当然，4.3和4.4泄露问题不能够解决，基于现在的18、19和20版本的是用户量，决定保证L版本以上不泄露即可
- 3、为了兼容18、19和20的圆角可以生效，GeneralRoundViewImpl内部会进行版本去选择RoundViewPolicy

### 什么？，你想快速集成，但又不想要那么多代码？(L版本及以上)

具体可以参考GeneralRoundView21Policy类实现，其实本质上只有几行代码，但是为了写的优雅嘛啊哈，你懂的


- 1、在你自定义view的dispatchDraw方法中直接使用ViewOutlineProvider

```

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, mContainer.width, mContainer.height, mCornerRadius);
            }
        });
    }
    
```

## MIT License

Copyright (c) 2019 minminaya

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.