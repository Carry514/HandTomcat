package org.gao.page01;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-27 15:17
 * @Description: 线程的实现方式之二：实现 Runnable 接口，通过 new Thread(runnable) 启动
 * @Version: 1.0
 */
public class MyRunnable implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
//            // 阻塞
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            System.out.println(Thread.currentThread().getName() + " " + i);
        }
        System.out.println("死亡");
    }

    public static void main(String[] args) {
        MyRunnable myRunnable1 = new MyRunnable();
        MyRunnable myRunnable2 = new MyRunnable();

        new Thread(myRunnable1).start();
        new Thread(myRunnable2).start();
    }
}
