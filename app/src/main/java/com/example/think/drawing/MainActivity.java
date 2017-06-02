package com.example.think.drawing;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private DrawView drawView;
    private ImageView pencil;
    private ImageView colorChoose;
    private ImageView widthChoose;
    private ImageView alphaChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView=(DrawView)findViewById(R.id.activity_paint_pv) ;
        pencil=(ImageView)findViewById(R.id.activity_paint_pencil) ;
        colorChoose=(ImageView)findViewById(R.id.activity_paint_color);
        widthChoose=(ImageView)findViewById(R.id.activity_paint_width);
        alphaChoose=(ImageView)findViewById(R.id.activity_paint_alpha) ;
        initMenu();
    }

    /**
     * 初始化底部菜单
     */
    private void initMenu() {
        //撤销
        menuItemSelected(R.id.activity_paint_undo, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                drawView.undo();
            }
        });

        //恢复
        menuItemSelected(R.id.activity_paint_redo, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                drawView.redo();
            }
        });

        //清空
        menuItemSelected(R.id.activity_paint_clear, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                drawView.clearAll();

            }
        });

        //橡皮擦
        menuItemSelected(R.id.activity_paint_eraser, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                drawView.setEraserModel(true);
            }
        });
        //进入绘画模式
        menuItemSelected(R.id.activity_paint_pencil, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                drawView.setPaintModel();
            }
        });
        //保存
        menuItemSelected(R.id.activity_paint_save, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                drawView.save();
                new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("已保存").setPositiveButton("确定",null ).create().show();
            }
        });

        //透明度
        menuItemSelected(R.id.activity_paint_alpha, new MenuSelectedListener() {
            @Override
            public void onMenuSelected() {
                PopupMenu popup = new PopupMenu(MainActivity.this, alphaChoose);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.alphamenu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.alpha_10:
                                drawView.setAlpha(25);break;
                            case R.id.alpha_30:
                                drawView.setAlpha(76);break;
                            case R.id.alpha_50:
                                drawView.setAlpha(127);break;
                            case R.id.alpha_70:
                                drawView.setAlpha(178);break;
                            case R.id.alpha_100:
                                drawView.setAlpha(255);break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        //颜色
        colorChoose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                PopupMenu popup = new PopupMenu(MainActivity.this, colorChoose);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.colormenu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.red:
                                pencil.setImageResource(R.drawable.pencilred);
                                drawView.setPaintColor(Color.RED);break;
                            case R.id.orange:
                                pencil.setImageResource(R.drawable.pencilorange);
                                drawView.setPaintColor(drawView.getResources().getColor(R.color.orange));break;
                            case R.id.yellow:
                                pencil.setImageResource(R.drawable.pencilyellow);
                                drawView.setPaintColor(Color.YELLOW);break;
                            case R.id.green:
                                pencil.setImageResource(R.drawable.pencilgreen);
                                drawView.setPaintColor(Color.GREEN);break;
                            case R.id.blueGreen:
                                pencil.setImageResource(R.drawable.pencilbg);
                                drawView.setPaintColor(drawView.getResources().getColor(R.color.cyan));break;
                            case R.id.blue:
                                pencil.setImageResource(R.drawable.pencilblue);
                                drawView.setPaintColor(Color.BLUE);break;
                            case R.id.purple:
                                pencil.setImageResource(R.drawable.pencilpurple);
                                drawView.setPaintColor(drawView.getResources().getColor(R.color.purple));break;
                            case R.id.gray:
                                pencil.setImageResource(R.drawable.pencilgray);
                                drawView.setPaintColor(Color.GRAY);break;
                            case R.id.black:
                                pencil.setImageResource(R.drawable.pencil);
                                drawView.setPaintColor(Color.BLACK);break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        //粗细
        widthChoose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                PopupMenu popup = new PopupMenu(MainActivity.this, widthChoose);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.toolsmenu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.width_1:
                                drawView.setStrokeWidth(2f);break;
                            case R.id.width_2:
                                drawView.setStrokeWidth(6f);break;
                            case R.id.width_3:
                                drawView.setStrokeWidth(12f);break;
                            case R.id.width_4:
                                drawView.setStrokeWidth(20f);break;
                            case R.id.width_5:
                                drawView.setStrokeWidth(25f);break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }


    /**
     * 选中底部 Menu 菜单项
     */
    private void menuItemSelected(int viewId, final MenuSelectedListener listener) {
        findViewById(viewId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMenuSelected();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    interface MenuSelectedListener {
        void onMenuSelected();
    }

}
