# XCLoadingImageView
XCLoadingImageView-加载进度动画View
效果图如下：


![image](https://github.com/jczmdeveloper/XCLoadingImageView/blob/master/screenshots/01.gif)

用法如下：

XCLoadingImageView imageView2 = (XCLoadingImageView) findViewById(R.id.img2);
imageView2.setMaskOrientation(XCLoadingImageView.MaskOrientation.LeftToRight);

imageView2.setProgress(40);
