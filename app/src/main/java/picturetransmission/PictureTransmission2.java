package picturetransmission;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import Veriable.TransmissionVeriable;

public class PictureTransmission2 extends AppCompatActivity
{
    public Bitmap UdpAssemBitmap = null;                                //udp接收完成使用位图定义
    public Bitmap UdpAssemBitmap2 = null;                                //udp接收完成使用位图定义
    public Boolean BT_OnOffFlag1 = false;                                //udp接收线程启停位
    public Boolean BT_OnOffFlag2 = false;                                //udp接收线程启停位
    public Boolean IP_CheckFlag = true;                                 //IP检查标志位，只开机检查1次
    public volatile Boolean Thread_RunningFlag1 = false;                 //UDP接收线程运行标志位
    public volatile Boolean Thread_RunningFlag2 = false;                 //UDP接收线程运行标志位
    public ImageView IMG_imageView1;
    public ImageView IMG_imageView2;

    /*** UDP客户端IP和端口**/
    public String UDPClientIP = "192.168.1.1";    //客户端IP
    public int UDPClientPort = 11000;                //客户端端口
    /*** UDP服务端IP和端口**/   //注：这里只用端口号就可以了，不用ip地址
    public int UDPSeverPort1 = 65000;                 //服务端端口
    public int UDPSeverPort2 = 12000;                 //服务端端口
    /**
     * UDP客户端通信定义
     **/
    DatagramSocket UDPSendSocket = null;             //定义UDP 发送socket
    InetAddress UDPClientAddress = null;             //客户端端IP
    /**
     * UDP服务端通信定义
     **/
    DatagramSocket UDPReceiveSocket = null;         //定义UDP 接收socket
    InetAddress UDPSeverAddressIP = null;           //服务端端IP
    DatagramSocket UDPReceiveSocket2 = null;         //定义UDP 接收socket
    InetAddress UDPSeverAddressIP2 = null;           //服务端端IP


    /************************************************************ ************************************************************ ************************************************************ /
     /************************************************************ ......................Handler此种形式需在主线程开始前定义...................... ************************************************************/
    /************************************************************ ************************************************************ ************************************************************/
    private Handler myHandler1 = new Handler(Looper.myLooper())//花：这里就是展示图片了，用bitmap格式调用setImageBitmap()函数显示图片
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    IMG_imageView1.setImageBitmap((Bitmap) msg.obj);   //图像显示
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    private Handler myHandler2 = new Handler(Looper.myLooper())
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    IMG_imageView2.setImageBitmap((Bitmap) msg.obj);   //图像显示
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };
//    t
    /************************************************************ ************************************************************ ************************************************************/
    /************************************************************ .......................主线程初始开始处...................... ************************************************************/
    /************************************************************ ************************************************************ ************************************************************/
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
//
//        Thread_RunningFlag1 = true;                                               //UDP接收标志位修改，线程使能
//        Runnable My_UDP_Receive_Runnable1 = new UDP_Receive_Runnable1();           //创建My_UDP_Receive_Runnable对象
//        Thread UdpRecThread1 = new Thread(My_UDP_Receive_Runnable1);               //UDP数据接收线程创建并启动
//        UdpRecThread1.start();
//
//        Thread_RunningFlag2 = true;                                               //UDP接收标志位修改，线程使能
//        Runnable My_UDP_Receive_Runnable2 = new UDP_Receive_Runnable2();           //创建My_UDP_Receive_Runnable对象
//        Thread UdpRecThread2 = new Thread(My_UDP_Receive_Runnable2);               //UDP数据接收线程创建并启动
//        UdpRecThread2.start();
//        /*ImageView监听事件注册...................*/
////        IMG_imageView1 = findViewById(R.id.imageView1);
////        IMG_imageView2 = findViewById(R.id.imageView2);
//    }
    public void startPictureTransmission()
    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Thread_RunningFlag1 = true;                                               //UDP接收标志位修改，线程使能
        Runnable My_UDP_Receive_Runnable1 = new UDP_Receive_Runnable1();           //创建My_UDP_Receive_Runnable对象
        Thread UdpRecThread1 = new Thread(My_UDP_Receive_Runnable1);               //UDP数据接收线程创建并启动
        UdpRecThread1.start();

        Thread_RunningFlag2 = true;                                               //UDP接收标志位修改，线程使能
        Runnable My_UDP_Receive_Runnable2 = new UDP_Receive_Runnable2();           //创建My_UDP_Receive_Runnable对象
        Thread UdpRecThread2 = new Thread(My_UDP_Receive_Runnable2);               //UDP数据接收线程创建并启动
        UdpRecThread2.start();
        /*ImageView监听事件注册...................*/
//        IMG_imageView1 = findViewById(R.id.imageView1);
//        IMG_imageView2 = findViewById(R.id.imageView2);
    }

    /*** ......................主线程终止处......................**/

    protected void onDestroy()
    {
        super.onDestroy();
    }




    /************************************************************ ************************************************************ ************************************************************/
    /************************************************************ ........................自写方法开始处......................  ************************************************************/
    /************************************************************ ************************************************************ ************************************************************/
    /*** ......................初始化窗口设置, 包括全屏、常亮、横屏......................**/
    private void initWindowSettings()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /*** ......................UDP接收数据线程......................**/
    class UDP_Receive_Runnable1 implements Runnable
    {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run()        //开启线程接收数据
        {
            try
            {
                System.out.println("线程测试1");

                boolean dataGetFlag = false;
                int picCopyIndex = 0;
                int buffSize = 0;
                String clientDatalengthStr;

                byte[] picData_buf = new byte[1024 * 100];                                                           //创建一个35kB的图像缓冲区（数据队列）
                byte[] udpReceive_buff = new byte[1024];                                                            //创建接收数据的数据buff

                UDPReceiveSocket = new DatagramSocket(UDPSeverPort1);                                                //创建服务接收端对象
                DatagramPacket receivePacket = new DatagramPacket(udpReceive_buff, udpReceive_buff.length);         //创建缓存区
                //Log.d("TAG", "接受数据： " + UDPSeverAddressIP + "   " + UDPSeverPort1 + "   " + "RECEIVE_THREAD_On: " + Thread_RunningFlag1);
//                System.out.println("线程测试2");

                while (Thread_RunningFlag1 == true)
                {
//                    System.out.println("线程测试3");

                    UDPReceiveSocket.receive(receivePacket);                                                       //接受数据
//                    System.out.println("线程测试4");

                    try
                    {
//                        System.out.println("线程测试5");

                        if (receivePacket.getLength() >= 2)        //如果接收数据大于2,则代表接收到数据
                        {
//                            !!!!!!!!!!!!!!!!!!!判断
//                            if (udpReceive_buff[0] == 'o' && udpReceive_buff[1] == 'k')
                            if (udpReceive_buff[0] == 'o')
                            {
                                dataGetFlag = true;                //当接收到ok信息了,表示马下就会接收下一个包,即图片大小信息包.同时上一次传送的图片信息已接收完毕了
//                                System.out.println("线程测试5");

                                if (picCopyIndex >= buffSize)
                                {
//////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////// picData_buf 是 字 节 格 式 ，这 里 也 可 以 直 接 调 用 ///////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////
                                    UdpAssemBitmap = BitmapFactory.decodeByteArray(picData_buf, 0, picData_buf.length); //字节数据输入流,生成位图
//////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////// 生 成 Bitmap 格 式 ，这里可以直接调用opencv来转化格式，参考链接   https://blog.csdn.net/mhhyoucom/article/details/107487847   ////////////////////
//////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////其实这里就可以把bitmap格式的数据返回了，但是我不知道要怎么搞，所以就只能麻烦森森了///////////////////////////////////////////////////////////////////////////////////
                                    TransmissionVeriable transmissionVeriable = TransmissionVeriable.getInstance();
                                    transmissionVeriable.setRightImage(UdpAssemBitmap);
                                    System.out.println("输出right完成");
//                                    Thread_RunningFlag1 = false;
                                    Message message = Message.obtain();
                                    message.what = 0;
                                    message.obj = UdpAssemBitmap;
//                                    myHandler1.sendMessage(message);
//////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////// 将数据位图发送到主线程，跳转到66行,下面的是按包接收数据的代码，1024bit为一包循环接收 /////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////

                                    UdpAssemBitmap = null;

                                    buffSize = 0;
                                    picCopyIndex = 0;

                                    for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                                    {
                                        udpReceive_buff[i] = 0;
                                    }
                                    for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                                    {
                                        picData_buf[j] = 0;
                                    }


                                }
                            }
                            else
                            {
                                if (dataGetFlag == true) //接收第二个包，内容为总数据长度
                                {
                                    dataGetFlag = false; //设置为false,表示这以后为第3个包了

                                    buffSize = 0;
                                    picCopyIndex = 0;

                                    clientDatalengthStr = new String((receivePacket.getData()), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                                    buffSize = Integer.parseInt(clientDatalengthStr); //解析出总数居的长度int

//                                Log.d("TAG", "图片接受clientData2: " + clientDatalengthStr + "图片接受buffSize: " + buffSize);
                                    for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                                    {
                                        udpReceive_buff[i] = 0;
                                    }
                                    for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                                    {
                                        picData_buf[j] = 0;
                                    }
                                }
                                else                                                 //开始接收并组合有效的数据包
                                {
                                    System.arraycopy((receivePacket.getData()), 0, picData_buf, picCopyIndex, receivePacket.getLength());
                                    picCopyIndex = picCopyIndex + receivePacket.getLength(); //将第3个包及以后的图片包信息保存于内存缓冲,即将图片包拼装起来组成一张图片信息.

//                                Log.d("TAG", "picCopyIndex: " + picCopyIndex);
                                }
                            }
                        }
                        else     //清空receivePacket的承接对象数组
                        {
                            for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                            {
                                udpReceive_buff[i] = 0;
                            }
                            for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                            {
                                picData_buf[j] = 0;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("TAG", "图片解析错误");
                        e.printStackTrace();

                        buffSize = 0;
                        picCopyIndex = 0;

                        for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                        {
                            udpReceive_buff[i] = 0;
                        }
                        for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                        {
                            picData_buf[j] = 0;
                        }
                    }
                    finally
                    {

                    }

                }
            }
            catch (Exception e)
            {
                Log.d("TAG", "UDPRec_Exception_Error: " + e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                UDPReceiveSocket.disconnect();
                UDPReceiveSocket.close();

                Thread_RunningFlag1 = false;                              //修改程创标志位，退出线程使能
                //BT_UdpOnOff1.setBackgroundResource(R.drawable.udp);       //背景图片切换OFF
                Log.d("TAG", "=====接受线程终止=====");
            }


        }
    }




/************************************************************ ******************************************************** ************************************************************/
/************************************************************ ..........这里重复上面的代码，接收并显示两个图片.......... ************************************************************/
    /************************************************************ ******************************************************** ************************************************************/
    class UDP_Receive_Runnable2 implements Runnable
    {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run()        //开启线程接收数据
        {
            try
            {
                boolean dataGetFlag2 = false;
                int picCopyIndex2 = 0;
                int buffSize2 = 0;String clientDatalengthStr2;

                byte[] picData_buf2 = new byte[1024 * 100];                                                           //创建一个35kB的图像缓冲区（数据队列）
                byte[] udpReceive_buff2 = new byte[1024];                                                            //创建接收数据的数据buff

                //UDPSeverAddressIP2 = InetAddress.getByName(UDPSeverIP2);                                              //获取IP地址
                UDPReceiveSocket2 = new DatagramSocket(UDPSeverPort2);                                                //创建服务接收端对象

                DatagramPacket receivePacket2 = new DatagramPacket(udpReceive_buff2, udpReceive_buff2.length);         //创建缓存区

                //Log.d("TAG", "接受数据： " + UDPSeverAddressIP2 + "   " + UDPSeverPort2 + "   " + "RECEIVE_THREAD_On: " + Thread_RunningFlag2);

                while (Thread_RunningFlag2 == true)
                {
                    UDPReceiveSocket2.receive(receivePacket2);                                                       //接受数据
                    try
                    {
                        if (receivePacket2.getLength() >= 2)        //如果接收数据大于2,则代表接收到数据
                        {
//                            if (udpReceive_buff2[0] == 'o' && udpReceive_buff2[1] == 'k')
                                if (udpReceive_buff2[0] == 'o')
                            {
                                dataGetFlag2 = true;                //当接收到ok信息了,表示马下就会接收下一个包,即图片大小信息包.同时上一次传送的图片信息已接收完毕了
                                if (picCopyIndex2 >= buffSize2)
                                {

                                    UdpAssemBitmap2 = BitmapFactory.decodeByteArray(picData_buf2, 0, picData_buf2.length); //字节数据输入流,生成位图
                                    TransmissionVeriable transmissionVeriable = TransmissionVeriable.getInstance();
                                    transmissionVeriable.setLeftImage(UdpAssemBitmap2);
                                    System.out.println("输出left完成");
//                                    Thread_RunningFlag2 = false;
                                    Message message2 = Message.obtain(); //将数据位图发送到主线程
                                    message2.what = 0;
                                    message2.obj = UdpAssemBitmap2;
//                                    myHandler2.sendMessage(message2);

                                    UdpAssemBitmap2 = null;

                                    buffSize2 = 0;
                                    picCopyIndex2 = 0;

                                    for (int i = 0; i < udpReceive_buff2.length; i++) //清空receivePacket的承接对象数组
                                    {
                                        udpReceive_buff2[i] = 0;
                                    }
                                    for (int j = 0; j < picData_buf2.length; j++) //清空picData_buf数组
                                    {
                                        picData_buf2[j] = 0;
                                    }
                                }
                            }
                            else
                            {
                                if (dataGetFlag2 == true) //接收第二个包，内容为总数据长度
                                {
                                    dataGetFlag2 = false; //设置为false,表示这以后为第3个包了

                                    buffSize2 = 0;
                                    picCopyIndex2 = 0;

                                    clientDatalengthStr2 = new String((receivePacket2.getData()), 0, receivePacket2.getLength(), StandardCharsets.UTF_8);
                                    buffSize2 = Integer.parseInt(clientDatalengthStr2); //解析出总数居的长度int

//                                Log.d("TAG", "图片接受clientData2: " + clientDatalengthStr + "图片接受buffSize: " + buffSize);
                                    for (int i = 0; i < udpReceive_buff2.length; i++) //清空receivePacket的承接对象数组
                                    {
                                        udpReceive_buff2[i] = 0;
                                    }
                                    for (int j = 0; j < picData_buf2.length; j++) //清空picData_buf数组
                                    {
                                        picData_buf2[j] = 0;
                                    }
                                }
                                else                                                 //开始接收并组合有效的数据包t
                                {
                                    System.arraycopy((receivePacket2.getData()), 0, picData_buf2, picCopyIndex2, receivePacket2.getLength());
                                    picCopyIndex2 = picCopyIndex2 + receivePacket2.getLength(); //将第3个包及以后的图片包信息保存于内存缓冲,即将图片包拼装起来组成一张图片信息.

//                                Log.d("TAG", "picCopyIndex: " + picCopyIndex);
                                }
                            }
                        }
                        else     //清空receivePacket的承接对象数组
                        {
                            for (int i = 0; i < udpReceive_buff2.length; i++) //清空receivePacket的承接对象数组
                            {
                                udpReceive_buff2[i] = 0;
                            }
                            for (int j = 0; j < picData_buf2.length; j++) //清空picData_buf数组
                            {
                                picData_buf2[j] = 0;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("TAG", "图片解析错误");
                        e.printStackTrace();

                        buffSize2 = 0;
                        picCopyIndex2 = 0;

                        for (int i = 0; i < udpReceive_buff2.length; i++) //清空receivePacket的承接对象数组
                        {
                            udpReceive_buff2[i] = 0;
                        }
                        for (int j = 0; j < picData_buf2.length; j++) //清空picData_buf数组
                        {
                            picData_buf2[j] = 0;
                        }
                    }
                    finally
                    {
                    }
                }
            }
            catch (Exception e)
            {
                Log.d("TAG", "UDPRec_Exception_Error: " + e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                UDPReceiveSocket2.disconnect();
                UDPReceiveSocket2.close();

                Thread_RunningFlag2 = false;                              //修改程创标志位，退出线程使能
                //BT_UdpOnOff2.setBackgroundResource(R.drawable.udp_cp);       //背景图片切换OFF
                Log.d("TAG", "=====接受线程终止=====");
            }
        }
    }
}