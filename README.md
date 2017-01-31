# EmotionKeyBoard
Android 带表情评论框，支持emoji,图片,自定义菜单view，根据软键盘高度自动调整

## 使用说名
布局以`SmileyInputRoot`为主布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<me.yngluo.emotionkeyboard.emotioninput.SmileyInputRoot xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    android:orientation="vertical">

    <... your view 设置weight=1/>

    <include layout="@layout/my_input_bar" />

</me.yngluo.emotionkeyboard.emotioninput.SmileyInputRoot>
```
Activity设置相关点击事件

```java
SmileyInputRoot rootViewGroup = (SmileyInputRoot) findViewById(R.id.root);
mPanelRoot = rootViewGroup.getmPanelLayout();

KeyboardUtil.attach(this, mPanelRoot, new KeyboardUtil.OnKeyboardShowingListener() {
    @Override
    public void onKeyboardShowing(boolean isShowing) {
        //键盘状态改变回掉
        Log.e("key board", String.valueOf(isShowing));
    }
});

mPanelRoot.init(input, smileyBtn, btnSend);
```
设置菜单view[可选],传入自定义view如`my_smiley_menu`

```java
mPanelRoot.setMoreView(LayoutInflater.from(this).inflate(R.layout.my_smiley_menu, null), btnMore);
```
自定义表情
修改`array.xml`按照格式添加你的表情,表情文件放在`asserts/smiley`目录,再修改`SmileyView`文件如下例

```java
List<SmileyDataSet> smileys = new ArrayList<>();
SmileyDataSet setTieba = SmileyDataSet.getDataSet(context, "贴吧", true, R.array.smiley_tieba);
SmileyDataSet setAcn = SmileyDataSet.getDataSet(context, "ac娘", true, R.array.smiley_acn);
SmileyDataSet setJgz = SmileyDataSet.getDataSet(context, "金馆长", true, R.array.smiley_jgz);
SmileyDataSet setYwz = SmileyDataSet.getDataSet(context, "颜文字", false, R.array.smiley_ywz);

smileys.add(setTieba);
smileys.add(setAcn);
smileys.add(setJgz);
smileys.add(SmileyEmoji.getEmojis());
smileys.add(setYwz);
setSmileys(smileys);
```
## 软件截图
![image](https://github.com/freedom10086/EmotionKeyBoard/blob/master/art/1.jpg)
![image](https://github.com/freedom10086/EmotionKeyBoard/blob/master/art/2.jpg)
![image](https://github.com/freedom10086/EmotionKeyBoard/blob/master/art/3.jpg)
![image](https://github.com/freedom10086/EmotionKeyBoard/blob/master/art/4.jpg)
![image](https://github.com/freedom10086/EmotionKeyBoard/blob/master/art/5.jpg)

暂无  
## 意见和反馈
- freedom10086 <yangluo.chn@gmail.com>

## 参考项目
[JKeyboardPanelSwitch](https://github.com/Jacksgong/JKeyboardPanelSwitch)
