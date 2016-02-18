package com.example.wx091.allfiles.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wx091.allfiles.R;
import com.example.wx091.allfiles.interfaces.ItemOptionInterface;


public class CustomDialogWithEditText extends Dialog {

	public CustomDialogWithEditText(Context context) {
		super(context);
	}

	public CustomDialogWithEditText(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private ItemOptionInterface itemOptionInterface;
		private Context context;
		private String title="";
		private String message="";
		private String type="";
		public EditText e;
		private String positiveButtonText="确定";
		private String negativeButtonText="取消";
		private View contentView;
		private OnClickListener positiveButtonClickListener=new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(type.equals("title"))
				{
					if(message.equals(e.getText().toString())){

					}else{
						itemOptionInterface.TitleChange(e.getText().toString());
					}
				}else if(type.equals("describe")){
					if(message.equals(e.getText().toString())){

					}else{
						itemOptionInterface.DescribeAdd(e.getText().toString());
					}

				}

				dialog.dismiss();
			}
		};
		private OnClickListener negativeButtonClickListener=new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		LayoutInflater inflater;
		View layout;

		public Builder(Context context) {
			this.context = context;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.dialog_edittext_layout, null);
			e=((EditText) layout.findViewById(R.id.dialog_generic_edittext));
		}

		public Builder setType(String type) {
			this.type = type;

			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			if(type.equals("title"))
			{
				title="修改标题";
			}else{
				title="添加描述";
			}
			return this;
		}

		public Builder setInterface(ItemOptionInterface itemOptionInterface){
			this.itemOptionInterface=itemOptionInterface;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public CustomDialogWithEditText create() {
			
			// instantiate the dialog with the custom Theme
			final CustomDialogWithEditText dialog = new CustomDialogWithEditText(context,R.style.Theme_AppCompat_Light_Dialog);
			
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.edittext_title)).setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			
			if (message != null) {
				e.setText(message);
				e.selectAll();
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
//				((LinearLayout) layout.findViewById(R.id.content))
//						.removeAllViews();
//				((LinearLayout) layout.findViewById(R.id.content)).addView(
//						contentView, new LayoutParams(
//								LayoutParams.FILL_PARENT,
//								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
