#### 1.介绍

1. 后端代码基于吴老师代码以及自己的风格完成。
2. 前端界面由于个人能力有限参考记事本修改而得，十分感谢。记事本项目地址:https://gitee.com/WD_MoonMoonBird/EasyPad
3. 经过一学期编译原理学习，收获颇多，深知编程之路任重而道远。
4. 运行函数MainForm，打成jar运行需要在jar的同级目录有resources文件夹
5. 整数和浮点数都集成为num，打印函数print，输入readInt，readBool，readString
#### 2.缺点
1. 代码编写非常丑陋，递归下降法下降了个寂寞
2. 仅仅支持if else 语句，不支持 if else多层嵌套
3. 不支持作用域，所以仅支持一层while语句（scope文件夹为作用域代码，待做）
4. 此项目仅用于完成作业

#### 3.修复
1. 不支持print(1 + 2 + "wwk");，原因扫到前俩个数字时会执行加法运算
   1. 解决：仅当两个节点都是Num类型时才进行数值的相加，(不太行)这样print(1 + 2 + 3);会正确
   2. 解决：看字符串转换成数字是否异常
#### 4. 示例代码
   ```java
   num a = 1,b = 0;
print(a + b);
print(a - b);
print(a * b);
print(a / b);

num c = 2, d = 3;
print("乘方:" + c ** d);
print("取余:" + c % d);
   ```
```java
num a = 0,result = 0;
while(a < 10) {
  if (a == 5) {
    break;
  }
  a = a + 1;
  result = result + a;
}
print("result:" + result);
```

![](.\src\main\resources\images\运行效果图1.png)
![](.\src\main\resources\images\运行效果图2.png)
