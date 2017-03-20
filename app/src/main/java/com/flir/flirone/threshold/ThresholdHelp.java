package com.flir.flirone.threshold;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.flir.flirone.R;

/**
 * Created by txiaozhe on 09/02/2017.
 */


public class ThresholdHelp {

    private int threshold_low;
    private int threshold_high;
    private EditText picker1, picker2;
    private SharedPreferences sp;
    private Button showDialog;

    private Context context;

    public int getThreshold_low() {
        return threshold_low;
    }

    public int getThreshold_high() {
        return threshold_high;
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public EditText getPicker1() {
        return picker1;
    }

    public EditText getPicker2() {
        return picker2;
    }

    public void setThreshold_low(int threshold_low) {
        this.threshold_low = threshold_low;
    }

    public void setThreshold_high(int threshold_high) {
        this.threshold_high = threshold_high;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    public ThresholdHelp(Context context, Button showDialog) {
        this.context = context;
        this.showDialog = showDialog;
    }

    public void showAddDialog() {

        LayoutInflater factory = LayoutInflater.from(context);
        final View pickersView = factory.inflate(R.layout.number_pickers, null);
        picker1 = (EditText) pickersView.findViewById(R.id.picker1);
        picker2 = (EditText) pickersView.findViewById(R.id.picker2);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
        ad1.setTitle("设置报警阈值：");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(pickersView);
        ad1.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                boolean exc = false;
                int low = sp.getInt("low", 20);
                int high = sp.getInt("high", 40);
                try {
                    String lowText = picker1.getText().toString();
                    String highText = picker2.getText().toString();
                    low = Integer.parseInt(lowText);
                    high = Integer.parseInt(highText);
                } catch (Exception e) {
                    exc = true;
                }


                //低阈值高于高阈值时
                if (low >= high || exc) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(context);
                    normalDialog.setIcon(android.R.drawable.ic_dialog_info);
                    normalDialog.setTitle("警告");
                    normalDialog.setMessage("您设的阈值不符合规范！请重新设置！");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //...To-do
                                }
                            });
                    normalDialog.show();
                } else {
                    threshold_low = low;
                    threshold_high = high;
                    showDialog.setText("设置阈值\n当前：" + threshold_low + ", " + threshold_high);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("low", low);
                    editor.putInt("high", high);
                    editor.commit();
                }
            }
        });
        ad1.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框

    }

    public void setThreshold() {
        sp = context.getSharedPreferences("threshold", Context.MODE_PRIVATE);

        threshold_low = sp.getInt("low", 20);
        threshold_high = sp.getInt("high", 40);

        showDialog.setText("设置阈值\n当前：" + threshold_low + ", " + threshold_high);
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
                picker1.setHint(threshold_low + "");
                picker2.setHint(threshold_high + "");
            }
        });
    }
}
