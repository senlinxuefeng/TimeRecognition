package com.yumingchuan.timerecognition;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yumingchuan.timerecognition.nlp.TimeNormalizer;
import com.yumingchuan.timerecognition.nlp.TimeUnit;
import com.yumingchuan.timerecognition.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private EditText et_content;
    private Context context;
    private Button btn;


    private TimeNormalizer normalizer;
    private String tempCharSequence;
    private List listTimeUnit;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        text = (TextView) findViewById(R.id.text);
        et_content = (EditText) findViewById(R.id.et_content);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeNormalizer normalizer = new TimeNormalizer(context, "TimeExp.m", true);

                normalizer.parse(et_content.getText().toString());// 抽取时间
                TimeUnit[] unit = normalizer.getTimeUnit();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < unit.length; i++) {
                    sb.append(DateUtil.formatDateDefault(unit[i].getTime()) + "-" + unit[i].getIsAllDayTime() + "-" + unit[i].Time_Expression + "\n");

                }
                text.setText(sb.toString());
            }
        });

        listTimeUnit = new ArrayList();

        normalizer = new TimeNormalizer(context, "TimeExp.m", true);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dealEditTextCallBack(et_content, charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    /**
     * 处理内容改变的回调
     *
     * @param charSequence
     */
    private void dealEditTextCallBack(EditText editText, CharSequence charSequence) {
        if (tempCharSequence == null || (!TextUtils.isEmpty(charSequence) && !tempCharSequence.equals(charSequence.toString()))) {
            String tempStr = charSequence.toString();
            normalizer.parse(tempStr);// 抽取时间
            TimeUnit[] unit = normalizer.getTimeUnit();
            if (unit.length > 0) {
                listTimeUnit.clear();
                for (int j = 0; j < unit.length; j++) {
                    int[][] locationArr = new int[1][2];
                    locationArr[0][0] = normalizer.target.indexOf(unit[j].Time_Expression);
                    //unit[j]._tp_origin
                    Log.i("TimeAnalyseTest", locationArr[0][0]+""+unit[j].Time_Expression);
                    locationArr[0][1] = normalizer.target.indexOf(unit[j].Time_Expression) + unit[j].Time_Expression.length();
                    Log.i("TimeAnalyseTest", locationArr[0][1]+"");
                    listTimeUnit.add(locationArr);
                }
                setForegroundColorSpan(editText, tempStr, listTimeUnit, true);
            } else {
                setForegroundColorSpan(editText, tempStr, listTimeUnit, false);
            }
        }
    }

    /**
     * 设置span，也就是高亮文字的颜色：这里只只设置了前景色，也可以设置其他的，未列出来
     *
     * @param editText
     * @param str
     * @param list
     * @param isHaveSpan
     */
    private void setForegroundColorSpan(EditText editText, String str, List list, boolean isHaveSpan) {
        tempCharSequence = str;
        int position = editText.getSelectionStart();
        if (isHaveSpan) {
            SpannableString spanString = new SpannableString(str);
            for (int i = 0; i < list.size(); i++) {
                spanString.setSpan(new ForegroundColorSpan(Color.BLUE), ((int[][]) list.get(i))[0][0], ((int[][]) list.get(i))[0][1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            editText.setText("");
            editText.append(spanString);
        } else {
            editText.setText(str);
        }
        editText.setSelection(position);
    }

}
