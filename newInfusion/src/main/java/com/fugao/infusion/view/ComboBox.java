package com.fugao.infusion.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fugao.infusion.R;

public class ComboBox extends LinearLayout {

  private final static String TAG = "ComboBox";

  private ListViewItemClickListener m_listener;

  private View m_view;
  private ListView m_listView;
  private PopupWindow m_popupwindow;
  private ListViewAdapter m_adapter_listview;
  private String[] m_data;
  private Context m_context;
  private Button m_Button;
  private EditText m_EditText;
  private LinearLayout m_Container;

  public ComboBox(Context context) {
    super(context);
    m_context = context;

    if (!isInEditMode()) {
      init();
    }
  }

  public ComboBox(Context context, AttributeSet attrs) {
    super(context, attrs);
    m_context = context;
    if (!isInEditMode()) init();
  }

  private void init() {
    View newView =
        LayoutInflater.from(m_context).inflate(R.layout.combobox_view_layout, this, true);
    m_Button = (Button) newView.findViewById(R.id.comboButton);
    m_Container = (LinearLayout) newView.findViewById(R.id.container);
    m_EditText = (EditText) newView.findViewById(R.id.comboEditText);

    m_adapter_listview = new ListViewAdapter(m_context);
    m_view = LayoutInflater.from(m_context).inflate(R.layout.combobox_listview, null);

    m_listView = (ListView) m_view.findViewById(R.id.id_listview);
    m_listView.setAdapter(m_adapter_listview);
    m_listView.setClickable(true);
    m_listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        m_popupwindow.dismiss();
        m_EditText.setText(m_data[position]);

        if (m_listener != null) {
          m_listener.onItemClick(position);
        }
      }
    });

    setListeners();
  }

  /**
   * 获取EditText
   * @return
   */
  public EditText getEditText() {
    return this.m_EditText;
  }

  /**
   * 获取Button
   * @return
   */
  public Button getButton() {
    return this.m_Button;
  }

  public LinearLayout getContainer() {
    return this.m_Container;
  }

  /**
   * 设置button的大小
   *
   * @Title: setButtonWidth
   * @Description: TODO
   * @return: void
   */
  public void setButtonWidth(int width) {
    m_Button.setWidth(width);
  }

  public void setData(String[] data) {
    if (null == data || data.length <= 0) {
      return;
    }

    m_data = data;
    // m_EditText.setText(data[0]);
  }

  public void setListViewOnClickListener(ListViewItemClickListener listener) {
    m_listener = listener;
  }

  public void setInputType(int inputType) {
    m_EditText.setInputType(inputType);
  }

  public void setEditHint(String hitString) {
    m_EditText.setHint(hitString);
  }

  private void setListeners() {
    m_Button.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return false;
      }
    });

    m_Container.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(TAG, "Click......");
        if (m_popupwindow == null) {
          if (m_data != null && m_data.length < 6) {
            m_popupwindow =
                new PopupWindow(m_view, ComboBox.this.getWidth(), LayoutParams.WRAP_CONTENT);
          } else {
            m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(),
                300);// LayoutParams.WRAP_CONTENT);
          }

          // 点击PopUpWindow外面的控件也可以使得PopUpWindow dimiss。
          // 需要顺利让PopUpWindow dimiss；PopUpWindow的背景不能为空。
          m_popupwindow.setBackgroundDrawable(new BitmapDrawable());

          // 获得焦点，并且在调用setFocusable（true）方法后，可以通过Back(返回)菜单使PopUpWindow
          // dimiss
          // pop.setFocusable(true)
          m_popupwindow.setFocusable(true);
          m_popupwindow.setOutsideTouchable(true);
          m_popupwindow.showAsDropDown(ComboBox.this, 0, 0);
        } else if (m_popupwindow.isShowing()) {
          m_popupwindow.dismiss();
        } else {
          m_popupwindow.showAsDropDown(ComboBox.this);
        }
        return false;
      }
    });

    m_Button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        Log.d(TAG, "Click......");
        if (m_popupwindow == null) {
          if (m_data != null && m_data.length < 6) {
            m_popupwindow =
                new PopupWindow(m_view, ComboBox.this.getWidth(), LayoutParams.WRAP_CONTENT);
          } else {
            m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(),
                300);// LayoutParams.WRAP_CONTENT);
          }

          // 点击PopUpWindow外面的控件也可以使得PopUpWindow dimiss。
          // 需要顺利让PopUpWindow dimiss；PopUpWindow的背景不能为空。
          m_popupwindow.setBackgroundDrawable(new BitmapDrawable());

          // 获得焦点，并且在调用setFocusable（true）方法后，可以通过Back(返回)菜单使PopUpWindow
          // dimiss
          // pop.setFocusable(true)
          m_popupwindow.setFocusable(true);
          m_popupwindow.setOutsideTouchable(true);
          m_popupwindow.showAsDropDown(ComboBox.this, 0, 0);
        } else if (m_popupwindow.isShowing()) {
          m_popupwindow.dismiss();
        } else {
          m_popupwindow.showAsDropDown(ComboBox.this);
        }
      }
    });
  }

  class ListViewAdapter extends BaseAdapter {
    private LayoutInflater m_inflate;

    public ListViewAdapter(Context context) {
      // TODO Auto-generated constructor stub
      m_inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
      // TODO Auto-generated method stub
      return m_data == null ? 0 : m_data.length;
    }

    @Override
    public Object getItem(int position) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public long getItemId(int position) {
      // TODO Auto-generated method stub
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // TODO Auto-generated method stub
      TextView textview = null;

      if (convertView == null) {
        convertView = m_inflate.inflate(R.layout.combobox_item, null);
        textview = (TextView) convertView.findViewById(R.id.id_txt);

        convertView.setTag(textview);
      } else {
        textview = (TextView) convertView.getTag();
      }

      textview.setText(m_data[position]);

      return convertView;
    }
  }

  public String getText() {
    return m_EditText.getText().toString();
  }

  public interface ListViewItemClickListener {
    void onItemClick(int position);
  }

    /**
     * 获得popupWindow
     * @return
     */
  public PopupWindow getpopupWindow(){
      return m_popupwindow;
  }


    public void showPopuWindow(){
        if (m_popupwindow == null) {
            if (m_data != null && m_data.length < 6) {
                m_popupwindow =
                        new PopupWindow(m_view, ComboBox.this.getWidth(), LayoutParams.WRAP_CONTENT);
            } else {
                m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(),
                        300);// LayoutParams.WRAP_CONTENT);
            }

            // 点击PopUpWindow外面的控件也可以使得PopUpWindow dimiss。
            // 需要顺利让PopUpWindow dimiss；PopUpWindow的背景不能为空。
            m_popupwindow.setBackgroundDrawable(new BitmapDrawable());

            // 获得焦点，并且在调用setFocusable（true）方法后，可以通过Back(返回)菜单使PopUpWindow
            // dimiss
            // pop.setFocusable(true)
            m_popupwindow.setFocusable(true);
            m_popupwindow.setOutsideTouchable(true);
            m_popupwindow.showAsDropDown(ComboBox.this, 0, 0);
        } else if (m_popupwindow.isShowing()) {
            m_popupwindow.dismiss();
        } else {
            m_popupwindow.showAsDropDown(ComboBox.this);
        }
    }
}
