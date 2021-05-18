# AbstractSwipeLayout
抽象的下拉刷新/上拉加载控件
### 背景
几乎每个APP都会有这个功能，并且设计也是五花八门，似乎在比谁更好看，基于设计的各种需求网上找的现成项目难免不合适，所以我把它做动画的部分给抽象出来，希望能满足大部分的设计需求。
### 预览
<img src="https://github.com/Dahuoji-Coder/AbstractSwipeLayout/blob/main/Screen_Video.gif?raw=true" width="300" />

### HeaderView实现
下拉刷新需要自定义一个HeaderView，然后实现IHeaderView接口就可以实现各种效果了
```java
public class MyHeaderView extends LinearLayout implements IHeaderView {

    private TextView textView;
    private final int height = 140;
    private boolean islLoading = false;

    public MyHeaderView(Context context) {
        super(context);
        init(context);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //my_header_view中只有一个TextView，就不贴代码了
        inflate(context, R.layout.my_header_view, this);
        textView = findViewById(R.id.textView);
    }

    @Override
    public int getHeaderHeight() {
        //注意：使用时需要一个固定值的高度
        return height;
    }

    @Override
    public void move(float dY) {
        if (islLoading) return;

        if (Math.abs(dY) >= height) {
            textView.setText("松手");
        } else {
            textView.setText("下拉刷新 " + dY);
        }
    }

    @Override
    public void loading() {
        islLoading = true;
        textView.setText("加载中");
    }

    @Override
    public void complete() {
        islLoading = false;
        textView.setText("完成");
    }

}
```
### FooterView实现
和HeaderView一模一样
### ContentView实现
ContentView需要实现的是IContentView，需要返回一下使用的滑动控件是否已经滑动到顶部或者底部（demo中以ScrollView为例）
```java
public class MyContentView extends LinearLayout implements IContentView {

    private ScrollView scrollView;

    public MyContentView(Context context) {
        super(context);
        init(context);
    }

    public MyContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //my_content_view里只有一个简单的纵向的ScrollView，也不贴代码了
        inflate(context, R.layout.my_content_view, this);
        scrollView = findViewById(R.id.scrollView);
    }

    @Override
    public boolean canSwipeUp() {
        //RecyclerView也是这么用，都是原生控件自带的方法
        return scrollView.canScrollVertically(1);
    }

    @Override
    public boolean canSwipeDown() {
        //RecyclerView也是这么用，都是原生控件自带的方法
        return scrollView.canScrollVertically(-1);
    }

}
```
### 准备工作就完成了,使用就更简单了
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AbstractSwipeLayout abstractSwipeLayout = findViewById(R.id.abstractSwipeLayout);
        MyContentView myContentView = new MyContentView(this);
        MyHeaderView myHeaderView = new MyHeaderView(this);
        MyFooterView myFooterView = new MyFooterView(this);
        try {
            //把三个View传进去
            abstractSwipeLayout.addViews(myHeaderView, myFooterView, myContentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        abstractSwipeLayout.setOnPullListener(new AbstractSwipeLayout.OnPullListener() {
            @Override
            public void refresh() {
                //开始刷新
                abstractSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abstractSwipeLayout.complete();
                    }
                }, 3000);
            }

            @Override
            public void loadMore() {
                //开始加载更多
                abstractSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abstractSwipeLayout.complete();
                    }
                }, 3000);
            }
        });
    }
}
```
