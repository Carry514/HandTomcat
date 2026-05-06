package org.gao.page01;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-27 15:05
 * @Description: 线程的实现方式之一：继承 Thread
 * @Version: 1.0
 */
public class MyThread extends Thread{

    // 执行
    @Override
    public void run() {

        for (int i = 0; i < 20; i++) {
            // 阻塞
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(currentThread().getName() + " " + i);
        }
        System.out.println("死亡");
    }


    public static void main(String[] args) {
        // 创建线程
        MyThread myThread1 = new MyThread();
        MyThread myThread2 = new MyThread();

        // 就绪
        myThread1.start();
        myThread2.start();

    }
}
