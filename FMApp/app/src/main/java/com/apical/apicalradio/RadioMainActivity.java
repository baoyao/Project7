package com.apical.apicalradio;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
//import android.app.DvdControl;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.apical.apicalradio.HScrollViewGroup.Direction;
import com.example.fmapp.R;
import com.example.fmutil.FMUtil;


public class RadioMainActivity extends BaseActivity {
	private static final String TAG = "radio";
	private static final int REFLASH_TIME = 0x01; // ʱ�����ID
	private static final int SEND_RADIO_CTRL = 0x02; // ��������������ID
	private ApicalHardwareCtrl mApicalHawreCtrl; // apicalӲ�����ƽӿ�
	// Ĭ�ϵĳ�����Ϣ
	private static short MIN_CHANNEL = (short) 53.10;//87.50
	private static short MAX_CHANNEL = (short) 162.90;//108.00
	private static final String AM_MIN_CHANNEL = "531"; // AM��СƵ��
	private static final String AM_MAX_CHANNEL = "1629"; // AM��СƵ��
	private static final String FM_UNIT_HZ = "FM \nMHz"; // FM��λ
	private static final String AM_UNIT_HZ = "AM \nKHz"; // AM��λ
	// ������״̬����
	private static final byte STATUS1_SEEK_AS = 0x01; // �Զ��Ѵ������־����ʾAS����Auto
														// Store
	private static final byte STATUS1_SEEK_PS = 0x08; // Ԥ��������־����ʾPS����Preset
														// Scan
	private static final byte STATUS1_SEEK_AF = 0x10; // Ԥ��������־����ʾPS����Preset
														// Scan
	private static final byte STATUS1_SEEK_TA = 0x20; // Ԥ��������־����ʾPS����Preset
														// Scan
	private static final int STATUS1_SEEK_RDS = 0x80; // Ԥ��������־����ʾPS����Preset
														// Scan
	private static final byte STATUS2_SEEK_LOC = 0x02; // Զ��̱�־��0��ʾԶ�̣�1��ʾ���

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	}

	private static final byte STATUS2_SEEK_SCAN = 0x04; // ����Ԥ����־����ʾSCAN
	private static final byte STATUS2_SEEK_TMC = 0x08; // ��ͨ��̨��־��Ϊ1ʱ����ʾ��ǰ��̨Ϊ��̨ͨ
	private static final byte STATUS2_SEEK_ST = 0x20; // �������źţ�Ϊ1ʱ����ʾST�����������־��
	private static final byte STATUS2_SEEK_MONO = 0x40; // ���л���ǿ�Ƶ����ģʽʱ���˱�־Ϊ1����ʾMONO�����������

	// �ؼ�
	private SeekBar mSeekBar;
	private HScrollViewGroup hsView;
	private TextView mChannelNumShowTextView;
	private TextView mRadioTimeTextView;
	private boolean mShowTimeDot = true; // ����Ƿ���ʾԭ��
	private RadioController mRadioController; // �������������
	private RadioConfigure mRadioConfigure; // ������Ϣ

	private Byte mRadioBandType = 0;// �洢��̨����
	private static final int FM1 = 0x01; // FM1
	private static final int FM2 = 0x02; // FM2
	private static final int FM3 = 0x03; // FM3
	private static final int AM1 = 0x04; // AM1
	private static final int AM2 = 0x05; // Am2

	// 公模使用
	private int Freq_len = 0;
	private short mFreqNum[];
	private String freqValueString[];
	private int nCurPage = 0;
	private int nTotalPage = 0;

	private int num_one_page = 6;

	private Byte mCurFreqIndex = 0; // ��ǰƵ����Ĭ���б��������
	private short mCurFreq = 0; // ��ǰƵ��Ƶ��
	private String mCurFreqString = "";// ��ǰƵ��Ƶ���ַ�
	// qulingling
	public static byte mMBType = 0;

	// ����ĵ�̨�б�
	private TextView mRadioChannelSave1;
	private TextView mRadioChannelSave2;
	private TextView mRadioChannelSave3;
	private TextView mRadioChannelSave4;
	private TextView mRadioChannelSave5;
	private TextView mRadioChannelSave6;

	private TextView mRadioBandTypeTextView;// ��ʾ��̨���͵��ı��ؼ�
	private TextView mRaidoUnit; // Ƶ�ʵ�λ��FM��AM��һ��
	private TextView mRadioState; // ������
	private TextView mRadioFlag;
	private TextView mRadioStation; // ���Ƶ��
	private TextView mPtyStatus; // ���Ƶ��
	private TextView mAFStatus; // ���Ƶ��
	private TextView mTAStatus; // ���Ƶ��
	private TextView mTPStatus; // ���Ƶ��

	private ImageButton AFButton;
	private ImageButton PTYButton;
	private ImageButton TAButton;
	private ImageButton LOCImageButton;

	// qulingling
	private ImageView mImageSignal;

	private boolean mScanBrower = false; // �Ƿ��ڽ���ɨ��

	private long beginTime = 0;
	private long endTime = 0;

	private boolean bAudioSet = false;

	public static RadioMainActivity radioInstance = null;

	private AlertDialog mPtyDialog = null;
	private boolean bAFMode = false;
	private boolean bRDSSignal = false;

	private Timer mTimerMode = null;
	private TimerTask mTimerModeTask;
	private Runnable runnableUi;
	private Handler handler;

	short shortMin = 0;
	short shortMax = 0;

	private DrawPicNum picNum;
	// �����߳����洦����Ϣ������UI����
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFLASH_TIME:
				ReflashTime();
				break;
			case SEND_RADIO_CTRL:
				mRadioController.GetRadioCurFreq();
				break;
			default:
				break;
			}
		}
	};

	// ʱ������߳�
	class TimeThread extends Thread {
		@Override
		public ClassLoader getContextClassLoader() {
			return super.getContextClassLoader();
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = REFLASH_TIME; // ��ϢID
					mHandler.sendMessage(msg);// ÿ��1�뷢��һ��msg��mHandler
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}

	// ��������������
	class SendRadioCtrlThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(10 * 1000);
					Message msg = new Message();
					msg.what = SEND_RADIO_CTRL; // ��ϢID
					mHandler.sendMessage(msg);// ÿ��10�뷢��һ��msg��mHandler
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}

	// ����Service���ͻ��������������
	private BroadcastReceiver mRadioServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive(" + context + ", " + intent + ")");
			Bundle data = intent.getExtras();
			int cmd = data.getInt("cmd");
			Log.d(TAG, "QLLRadio onReceive() cmd = " + cmd + "   data = "
					+ data);
//			switch (cmd) {
//			case DvdControl.MCU_RET_FREQLIST:
//			case DvdControl.MCU_RET_SAVED_FREQ:
//				onEvent_RET_FreqList(data);
//				break;
//			case DvdControl.MCU_RET_CUR_FREQ:
//			case DvdControl.MCU_RET_CUR_FREQ_EX:
//				onEvent_RET_Current_Freq(data);
//				break;
//			case DvdControl.MCU_RET_RADIO_STATUS:
//				onEvent_RET_RADIO_STATE(data);
//				break;
//			case DvdControl.SETUP_MCU_RET_WORKMODE:
//				onEvent_RET_WORKMODE(data);
//				break;
//			case DvdControl.MCU_RET_RADIO_NAME:
//				onEvent_RET_RADIO_STATION(data);
//				break;
//			case DvdControl.MCU_RET_PTY_STATUS:
//				onEvent_RET_PTY_STATUS(data);
//				break;
//			case DvdControl.RADIO_MCU_RET_STEP:
//				onEvent_RET_MCU_STEP(data);
//				break;
//			case DvdControl.MCU_RET_RDS_EN:
//				onEvent_RET_RDS_EN(data);
//				break;
//			}
		}
	};

	private BroadcastReceiver mExitReceiver = new BroadcastReceiver() {
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(RADIO_EXIT)) {
				Log.d(TAG, "QLLRadio RADIO_EXIT");
				mRadioController.CloseRadio();
				finish();
			} else if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_BUTTON)) {
				KeyEvent event = (KeyEvent) intent
						.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
				if (event == null) {
					return;
				}

				switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_MEDIA_STOP:
					mRadioController.CloseRadio();
					finish();
					break;
				}
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ������ɨ�裬ֹͣ
		if (mScanBrower) {
			mScanBrower = false;
			mRadioController.RadioCtrl(RadioController.RADIO_STOP_SCAN);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FMUtil.onApplicationCreate();//add by bao
		Calendar ca = Calendar.getInstance();
		Log.d(TAG, "QLLRadio onCreate time=" + ca.get(Calendar.SECOND) + ":"
				+ ca.get(Calendar.MILLISECOND));

		// 消息注册
		IntentFilter filter = new IntentFilter(
				RadioService.ACTION_TYPE_RADIO_EVENT);
		filter.addAction(RadioService.ACTION_TYPE_APK_SETUP);
		registerReceiver(mRadioServiceReceiver, filter);

		IntentFilter exitfilter = new IntentFilter(RADIO_EXIT);
		// exitfilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		registerReceiver(mExitReceiver, exitfilter);

		mApicalHawreCtrl = new ApicalHardwareCtrl(ApicalHardwareCtrl.APP_RADIO);

		// 创建mRadioController
		InitRadioInterface();

		// 初始化PTY弹出框
		InitPtyDialog();

		// 启动service
		mRadioController.StartRadioService((byte) 0, null);
		RadioService.setEntryActivityClass(getClass());

		// 获取主板型号
		Bundle bundle;
		bundle = new Bundle();
		mRadioController.GetSysSettings("MbType", 1, bundle);
		mMBType = bundle.getByte("MbType");
		mRadioController.GetSysSettings("RDSEn", 1, bundle);
		byte mRDSEn = bundle.getByte("RDSEn");
		Log.d(TAG, "qulingling******************mRDSEn=" + mRDSEn);
		picNum = new DrawPicNum(RadioMainActivity.this);
		// ����ҳ������

		if (mMBType == DvdMBType.MB_XUGANG) {
			if (mRDSEn == 0x01) {
				setContentView(R.layout.activity_radio_xg_rds_main);
			} else {
				setContentView(R.layout.activity_radio_xg_main);
			}
		} else {
			// setContentView(R.layout.activity_radio_rds_main);
			setContentView(R.layout.activity_dlam_rds_main);
			/*
			 * if (mRDSEn == 0x01) {
			 * setContentView(R.layout.activity_radio_xg_rds_main); } else {
			 * setContentView(R.layout.activity_radio_rds_main); }
			 */
		}

		// ��ʼ���ؼ�
		InitCtrl();

		InitInterface();

		RdsDisp();

		// ���ó����������
		AppRunState.SetAppRun();

		// 启动定时器
		InitTimer();

		radioInstance = this;
	}

	/*
	 * qulingling 20131024 初始化定时器
	 */
	private void InitTimer() {
		handler = new Handler();
		mTimerMode = new Timer();
		mTimerModeTask = new TimerTask() {

			@Override
			public void run() {
				handler.post(runnableUi);
			}
		};
		mTimerMode.schedule(mTimerModeTask, 1000, 1000);

		runnableUi = new Runnable() {
			@Override
			public void run() {
				if (bAFMode == true) {
					if (bRDSSignal == true) {
						Calendar ca = Calendar.getInstance();
						int second = ca.get(Calendar.SECOND);
						Log.d(TAG, "QLLRadio second=" + second);
						if ((second % 2) == 0) {
							mAFStatus.setText("AF");
						} else {
							mAFStatus.setText("");
						}
					} else {
						mAFStatus.setText("AF");
					}
				} else {
					mAFStatus.setText("");
				}
			}

		};

	}

	// �����ʼ��
	private void InitRadioInterface() {
		// ����ʱ������߳�
		new TimeThread().start();

		mRadioConfigure = new RadioConfigure(this);
		mRadioController = mRadioConfigure.getRadioController();

	}

	/*
	 * qulingling 20130828 初始化界面控件
	 */
	private void InitInterface() {
		mSeekBar = (SeekBar) findViewById(R.id.seekBarProgress);
		mSeekBar.setMax((MAX_CHANNEL - MIN_CHANNEL)*100);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			float showFreq = 0.0f;
			int showNum = 0;
			boolean onTouchStick = false;

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.e(TAG, "mSeekBar.onProgressChanged(" + seekBar + ", "
						+ progress + ", " + fromUser + ")");
				progress = progress/100;
				if (onTouchStick) {
					// FM
					if ((mRadioBandType <= FM3) && (mRadioBandType >= FM1)) {
						int showNum = MIN_CHANNEL + progress;
						showFreq = showNum / 100.0f;
						mChannelNumShowTextView
								.setBackgroundDrawable(new BitmapDrawable(
										picNum.getFloatPic(showFreq)));
						// SetFMFreq(showFreq);
					} else {
						showNum = MIN_CHANNEL + progress;
						mChannelNumShowTextView
								.setBackgroundDrawable(new BitmapDrawable(
										picNum.getIntPic(showNum)));
						// SetAMFreq(showNum);
					}
				}

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.e(TAG, "mSeekBar.onStartTrackingTouch(" + seekBar + ")");

				onTouchStick = true;
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.e(TAG, "mSeekBar.onStopTrackingTouch(" + seekBar + ")");

				onTouchStick = false;
				// FM �������õ�Ƶ��
				if ((mRadioBandType <= FM3) && (mRadioBandType >= FM1)) {
					SetFMFreq(showFreq);
				} else {
					Log.v("tt","onStopTrackingTouch showNum: "+showNum);
					FMUtil.tune(showNum*100);
					SetAMFreq(showNum);
				}
			}
		});

		final ImageButton moveImageButton = (ImageButton) findViewById(R.id.move);
		moveImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (hsView.getCurScreen() > 0) {
					moveImageButton
							.setBackgroundResource(R.drawable.next_page_btn_selector);
					hsView.setDirection(Direction.LEFT);
					hsView.snapToScreen(0);
				} else {
					moveImageButton
							.setBackgroundResource(R.drawable.previous_page_btn_selector);
					hsView.setDirection(Direction.RIGHT);
					hsView.snapToScreen(1);
				}
			}
		});

		// FM/AM�л�
		ImageButton fMAMImageButton = (ImageButton) findViewById(R.id.buttonFmAm);
		fMAMImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "fMAMImageButton.onClick(" + view + ")");
				switch (mRadioBandType) {
				case FM1:
					mRadioController
							.RadioCtrl(RadioController.RADIO_SWITCH_FM2);
					break;
				case FM2:
					mRadioController
							.RadioCtrl(RadioController.RADIO_SWITCH_FM3);
					break;
				case FM3:
					mRadioController
							.RadioCtrl(RadioController.RADIO_SWITCH_AM1);
					break;
				case AM1:
					mRadioController
							.RadioCtrl(RadioController.RADIO_SWITCH_AM2);
					break;
				case AM2:
					mRadioController
							.RadioCtrl(RadioController.RADIO_SWITCH_FM1);
					break;
				default:
					break;
				}
			}
		});

		AFButton = (ImageButton) findViewById(R.id.buttonAF);
		AFButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRadioController.RadioCtrl(RadioController.RADIO_SWITCH_AF);
			}
		});
		TAButton = (ImageButton) findViewById(R.id.buttonTA);
		TAButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRadioController.RadioCtrl(RadioController.RADIO_SWITCH_TA);
			}
		});
		PTYButton = (ImageButton) findViewById(R.id.buttonPTY);
		PTYButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPtyDialog.show();
			}
		});

		// LOC
		LOCImageButton = (ImageButton) findViewById(R.id.buttonLOC);
		LOCImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "LOCImageButton.onClick(" + view + ")");
				mRadioController.RadioCtrl(RadioController.RADIO_LOC_SWITCH);
			}
		});

		// ������һ��
		ImageButton preChannelImageButton = (ImageButton) findViewById(R.id.buttonPreChannel);
		preChannelImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "preChannelImageButton.onClick(" + view + ")");
				mRadioController
						.RadioCtrl(RadioController.RADIO_HANDLE_SEARCH_UP);
			}
		});

		// ������һ��
		ImageButton nextChannelImageButton = (ImageButton) findViewById(R.id.buttonNextChannel);
		nextChannelImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "nextChannelImageButton.onClick(" + view + ")");
				mRadioController
						.RadioCtrl(RadioController.RADIO_HANDLE_SEARCH_DOWN);
			}
		});

		// 向前搜索
		ImageButton preSearchImageButton = (ImageButton) findViewById(R.id.buttonPreSearch);
		preSearchImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "preSearchImageButton.onClick(" + view + ")");
				mScanBrower = true;
				mRadioController
						.RadioCtrl(RadioController.RADIO_AUTO_SEARCH_UP);
			}
		});

		// 向后搜索
		ImageButton nextSearchImageButton = (ImageButton) findViewById(R.id.buttonNextSearch);
		nextSearchImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "nextSearchImageButton.onClick(" + view + ")");
				mScanBrower = true;
				mRadioController
						.RadioCtrl(RadioController.RADIO_AUTO_SEARCH_DOWN);

			}
		});

		// 自动搜索
		ImageButton autoSaveImageButton = (ImageButton) findViewById(R.id.buttonAutoSearch);
		autoSaveImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "autoSaveImageButton.onClick(" + view + ")");
				mScanBrower = true;
				mRadioController
						.RadioCtrl(RadioController.RADIO_AUTO_SEARCH_SAVE);
			}
		});

		// SCAN
		ImageButton scanBrowerImageButton = (ImageButton) findViewById(R.id.buttonBrower);
		scanBrowerImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "scanBrowerImageButton.onClick(" + view + ")");
				mScanBrower = true;
				mRadioController.RadioCtrl(RadioController.RADIO_FM_BROWER);
			}
		});

		// Equal
		ImageButton EqualImageButton = (ImageButton) findViewById(R.id.buttonEqual);
		EqualImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Log.d(TAG, "LOCImageButton.onClick(" + view + ")");
				startAudioSettings();
			}
		});

		// left
		ImageButton LeftButton = (ImageButton) findViewById(R.id.buttonLeft);
		LeftButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ListFreqPage(-1);
			}
		});

		// Equal
		ImageButton RightButton = (ImageButton) findViewById(R.id.buttonRight);
		RightButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ListFreqPage(1);
			}
		});

	}

	/*
	 * qulingling 20131024 PTY选择框
	 */
	private void InitPtyDialog() {
		Builder builder = new Builder(this);

		// 设置对话框的图标
		builder.setIcon(R.drawable.radio_icon);

		// 设置对话框的标题
		// builder.setTitle("列表对话框");

		builder.setItems(R.array.pty, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// String
				// hoddy=getResources().getStringArray(R.array.pty)[which];
				Log.d(TAG, "InitPtyDialog which = " + which);
				mRadioController.SetRadioPty((byte) which);
			}
		});

		// 创建一个列表对话框
		mPtyDialog = builder.create();

	}

	private void RdsDisp() {
		Bundle bundle;
		bundle = new Bundle();
		mRadioController.GetSysSettings("RDSEn", 1, bundle);
		byte mRDSEn = bundle.getByte("RDSEn");
		if (mRDSEn == 0x01) {
			// mSeekBar.setVisibility(View.GONE);
			TAButton.setEnabled(true);
			AFButton.setEnabled(true);
			PTYButton.setEnabled(true);
			LOCImageButton.setEnabled(true);
			mPtyStatus.setVisibility(View.VISIBLE);
			mAFStatus.setVisibility(View.VISIBLE);
			mTAStatus.setVisibility(View.VISIBLE);
			mTPStatus.setVisibility(View.VISIBLE);
			mRadioStation.setVisibility(View.VISIBLE);
		} else {
			// mSeekBar.setVisibility(View.VISIBLE);
			TAButton.setEnabled(false);
			AFButton.setEnabled(false);
			PTYButton.setEnabled(false);
			LOCImageButton.setEnabled(false);
			mPtyStatus.setVisibility(View.INVISIBLE);
			mAFStatus.setVisibility(View.GONE);
			mTAStatus.setVisibility(View.GONE);
			mTPStatus.setVisibility(View.GONE);
			mRadioStation.setVisibility(View.GONE);
		}
	}

	// �ؼ���ʼ��
	private void InitCtrl() {
		Log.d(TAG, "InitCtrl()---------->init TextView");

		hsView = (HScrollViewGroup) findViewById(R.id.hsView);

		mRadioChannelSave1 = (TextView) findViewById(R.id.FmChannel1);
		mRadioChannelSave2 = (TextView) findViewById(R.id.FmChannel2);
		mRadioChannelSave3 = (TextView) findViewById(R.id.FmChannel3);
		mRadioChannelSave4 = (TextView) findViewById(R.id.FmChannel4);
		mRadioChannelSave5 = (TextView) findViewById(R.id.FmChannel5);
		mRadioChannelSave6 = (TextView) findViewById(R.id.FmChannel6);

		mRadioChannelSave1.setOnTouchListener(textViewTouchListener);
		mRadioChannelSave2.setOnTouchListener(textViewTouchListener);
		mRadioChannelSave3.setOnTouchListener(textViewTouchListener);
		mRadioChannelSave4.setOnTouchListener(textViewTouchListener);
		mRadioChannelSave5.setOnTouchListener(textViewTouchListener);
		mRadioChannelSave6.setOnTouchListener(textViewTouchListener);

		// Ƶ������
		mRadioBandTypeTextView = (TextView) findViewById(R.id.FmType);
		// �м���ʾ��ǰƵ����textView
		mChannelNumShowTextView = (TextView) findViewById(R.id.FmNum);
		// ���������ʱ��
		mRadioTimeTextView = (TextView) findViewById(R.id.FmTime);

		// Ƶ�ʵ�λ
		mRaidoUnit = (TextView) findViewById(R.id.FmHz);
		// ������״̬
		mRadioState = (TextView) findViewById(R.id.FmState);

		mRadioFlag = (TextView) findViewById(R.id.FmFlag);

		// qulingling
		mImageSignal = (ImageView) findViewById(R.id.imageSignal);
		if (mMBType == DvdMBType.MB_05DVD || mMBType == DvdMBType.MB_05HDVD) {
			mImageSignal.setVisibility(View.VISIBLE);
		} else if (mMBType == DvdMBType.MB_XUGANG) {
			mImageSignal.setVisibility(View.INVISIBLE);
		}

		// qulingling 20131023
		mRadioStation = (TextView) findViewById(R.id.RadioStation);
		mPtyStatus = (TextView) findViewById(R.id.ptyStatus);
		mAFStatus = (TextView) findViewById(R.id.textViewAF);
		mTAStatus = (TextView) findViewById(R.id.textViewTA);
		mTPStatus = (TextView) findViewById(R.id.textViewTP);
	}

	// ����TextView��Touch�¼�
	public OnTouchListener textViewTouchListener = new OnTouchListener() {
		boolean bLongPress = false;

		@Override
		// qulingling 20130725
		public boolean onTouch(View v, MotionEvent event) {
			if (v == mRadioChannelSave1) {
				Log.d(TAG, "qulingling******************event.getAction()="
						+ event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					beginTime = System.currentTimeMillis();
					bLongPress = false;
					mRadioChannelSave1
							.setBackgroundResource(R.drawable.channel1_sel);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					endTime = System.currentTimeMillis();
					Log.d(TAG,
							"**********************qulingling******************endTime"
									+ endTime + "beginTime=" + beginTime);
					if ((endTime - beginTime) <= 2000) {
						PlayListRadio(0);
					}

					mRadioChannelSave1
							.setBackgroundResource(R.drawable.channel1);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (bLongPress == false) {
						endTime = System.currentTimeMillis();
						if ((endTime - beginTime) > 2000) {
							bLongPress = true;
							SaveCurFreq(0);
							mChannelNumShowTextView
									.setBackgroundDrawable(new BitmapDrawable(
											picNum.getStringPic(mCurFreqString)));
						}
					}
				}
			} else if (v == mRadioChannelSave2) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					beginTime = System.currentTimeMillis();
					bLongPress = false;
					mRadioChannelSave2
							.setBackgroundResource(R.drawable.channel2_sel);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					endTime = System.currentTimeMillis();
					Log.d(TAG,
							"**********************qulingling******************endTime"
									+ endTime + "beginTime=" + beginTime);
					if ((endTime - beginTime) <= 2000) {
						PlayListRadio(1);
					}
					mRadioChannelSave2
							.setBackgroundResource(R.drawable.channel2);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (bLongPress == false) {
						endTime = System.currentTimeMillis();
						if ((endTime - beginTime) > 2000) {
							bLongPress = true;
							SaveCurFreq(1);
							mChannelNumShowTextView
									.setBackgroundDrawable(new BitmapDrawable(
											picNum.getStringPic(mCurFreqString)));
						}
					}
				}
			} else if (v == mRadioChannelSave3) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					beginTime = System.currentTimeMillis();
					bLongPress = false;
					mRadioChannelSave3
							.setBackgroundResource(R.drawable.channel3_sel);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					endTime = System.currentTimeMillis();
					Log.d(TAG,
							"**********************qulingling******************endTime"
									+ endTime + "beginTime=" + beginTime);
					if ((endTime - beginTime) <= 2000) {
						PlayListRadio(2);
					}
					mRadioChannelSave3
							.setBackgroundResource(R.drawable.channel3);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (bLongPress == false) {
						endTime = System.currentTimeMillis();
						if ((endTime - beginTime) > 2000) {
							bLongPress = true;
							SaveCurFreq(2);
							mChannelNumShowTextView
									.setBackgroundDrawable(new BitmapDrawable(
											picNum.getStringPic(mCurFreqString)));
						}
					}
				}
			} else if (v == mRadioChannelSave4) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					beginTime = System.currentTimeMillis();
					bLongPress = false;
					mRadioChannelSave4
							.setBackgroundResource(R.drawable.channel4_sel);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					endTime = System.currentTimeMillis();
					Log.d(TAG,
							"**********************qulingling******************endTime"
									+ endTime + "beginTime=" + beginTime);
					if ((endTime - beginTime) <= 2000) {
						PlayListRadio(3);
					}
					mRadioChannelSave4
							.setBackgroundResource(R.drawable.channel4);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (bLongPress == false) {
						endTime = System.currentTimeMillis();
						if ((endTime - beginTime) > 2000) {
							bLongPress = true;
							SaveCurFreq(3);
							mChannelNumShowTextView
									.setBackgroundDrawable(new BitmapDrawable(
											picNum.getStringPic(mCurFreqString)));
						}
					}
				}
			} else if (v == mRadioChannelSave5) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					beginTime = System.currentTimeMillis();
					bLongPress = false;
					mRadioChannelSave5
							.setBackgroundResource(R.drawable.channel5_sel);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					endTime = System.currentTimeMillis();
					Log.d(TAG,
							"**********************qulingling******************endTime"
									+ endTime + "beginTime=" + beginTime);
					if ((endTime - beginTime) <= 2000) {
						PlayListRadio(4);
					}
					mRadioChannelSave5
							.setBackgroundResource(R.drawable.channel5);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (bLongPress == false) {
						endTime = System.currentTimeMillis();
						if ((endTime - beginTime) > 2000) {
							bLongPress = true;
							SaveCurFreq(4);
							mChannelNumShowTextView
									.setBackgroundDrawable(new BitmapDrawable(
											picNum.getStringPic(mCurFreqString)));
						}
					}
				}
			} else if (v == mRadioChannelSave6) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					beginTime = System.currentTimeMillis();
					bLongPress = false;
					mRadioChannelSave6
							.setBackgroundResource(R.drawable.channel6_sel);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					endTime = System.currentTimeMillis();
					Log.d(TAG,
							"**********************qulingling******************endTime"
									+ endTime + "beginTime=" + beginTime);
					if ((endTime - beginTime) <= 2000) {
						PlayListRadio(5);
					}
					mRadioChannelSave6
							.setBackgroundResource(R.drawable.channel6);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (bLongPress == false) {
						endTime = System.currentTimeMillis();
						if ((endTime - beginTime) > 2000) {
							bLongPress = true;
							SaveCurFreq(5);
							mChannelNumShowTextView
									.setBackgroundDrawable(new BitmapDrawable(
											picNum.getStringPic(mCurFreqString)));
						}
					}
				}
			}
			return false;
		}
	};

	// ʱ�����
	private void ReflashTime() {
		long sysTime = System.currentTimeMillis();
		CharSequence sysTimeStr;
		if (mShowTimeDot) {
			sysTimeStr = DateFormat.format("hh : mm", sysTime);
			mShowTimeDot = false;
		} else {
			sysTimeStr = DateFormat.format("hh   mm", sysTime);
			mShowTimeDot = true;
		}

		mRadioTimeTextView.setText(sysTimeStr); // ����ʱ��
	}

	// ��ȡƵ����Χ
	private int GetChannelRange() {
		return 0;
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "QLLRadio  onPause");
		super.onPause();
		if (true == bAudioSet) {
			return;
		}

		// release workmode
		if (AppRunState.GetHomeBack() == true) {
			mRadioController.BackWorkMode(mRadioController.RADIO_XG_MODE);
		} else {
			mRadioController.ReleaseWorkMode(mRadioController.RADIO_XG_MODE);
		}
	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "QLLRadio  onDestroy");
		unregisterReceiver(mRadioServiceReceiver);
		unregisterReceiver(mExitReceiver);
		AppRunState.SetAppStop();
		mTimerMode.cancel();

		super.onDestroy();

		FMUtil.onApplicationDestory();//add by bao
	}

	@Override
	protected void onResume() {
		super.onResume();
		Calendar ca = Calendar.getInstance();
		Log.d(TAG, "QLLRadio onResume time=" + ca.get(Calendar.SECOND) + ":"
				+ ca.get(Calendar.MILLISECOND));

		if (true == bAudioSet) {
			bAudioSet = false;
			return;
		}

		// ��Դ����
		StopMusic();
		mApicalHawreCtrl.AudioSourceRequest();

		mRadioController.GetRadioCurFreq();
		mRadioController.GetRadioCurState();
		mRadioController.GetRadioSaveFreq();
		mRadioController.InitRadio();

		// set radio mode
		if (this.getIntent() != null) {
			int num1 = this.getIntent().getIntExtra("setModeFlag", -1);
			if (num1 != 1) {
				mRadioController.SetRadioMode();
			}
		}

		AppRunState.SetHomeBack(false);

		Log.d(TAG, "QLLRadio onResume end time=" + ca.get(Calendar.SECOND)
				+ ":" + ca.get(Calendar.MILLISECOND));
	}

	// ��FM short�ֽ�תΪ�ַ�Ƶ��
	private String changeFreq2String(short Freq) {
		byte leftNum = (byte) ((Freq >> 8) & 0x00ff);
		byte RightNum = (byte) (Freq & 0x00ff);

		String tempFreq = " ";
		if (RightNum <= 9) {
			tempFreq = leftNum + "." + RightNum;
		} else {
			byte num = (byte) (RightNum / 10);
			tempFreq = leftNum + "." + num;
		}
		Log.d("dzt", "req = " + Freq + " left = " + leftNum + " right"
				+ RightNum + " fre" + tempFreq + " leng = " + tempFreq.length());
		// Log.e("***********","leftNum = "+leftNum + "  RightNum="+RightNum+
		// "  tempFreq"+tempFreq);
		return tempFreq;
	}

	/*
	 * qulingling 20131105
	 */
	private short changeFreq2Short(short Freq) {
		short shortFreq;

		byte leftNum = (byte) ((Freq >> 8) & 0x00ff);
		byte RightNum = (byte) (Freq & 0x00ff);

		shortFreq = (short) (leftNum * 100 + RightNum);

		return shortFreq;
	}

	// ��FM short�ֽ�תΪ����
	private int FMFreq2Int(short Freq) {
		byte leftNum = (byte) ((Freq >> 8) & 0x00ff);
		byte RightNum = (byte) (Freq & 0x00ff);
		// int tempFreq = (leftNum*10) + (RightNum/10);
		int tempFreq = (leftNum * 100) + RightNum;
		Log.e(TAG, "FMFreq2Int leftNum = " + leftNum + "  RightNum=" + RightNum
				+ "  tempFreq" + tempFreq);
		return tempFreq;
	}

	// ����FMƵ��
	private void SetFMFreq(float Freq) {
		float f1 = Freq % 1;
		byte a = (byte) (Freq - f1);
		byte b = (byte) (f1 * 100);
		mRadioController.SetRadioFreq(a, b);
		/*
		 * String valueString = String.valueOf(Freq); String
		 * sarray[]=valueString.split("."); if (sarray.length == 2) {
		 * mRadioController.SetRadioFreq(Byte.parseByte(sarray[0]),
		 * Byte.parseByte(sarray[1])); }
		 */
	}

	private void SetAMFreq(int Freq) {
		byte leftNum = (byte) ((Freq >> 8) & 0x00ff);
		byte RightNum = (byte) (Freq & 0x00ff);
		mRadioController.SetRadioFreq(leftNum, RightNum);
	}

	// ����������Ƶ������
	protected void SetRadioBand(Byte bandType) {
		mRadioBandType = bandType;
		int resid = 0;
		switch (bandType) {
		case FM1:
			resid = R.drawable.fm1;
			LOCImageButton.setEnabled(true);
			break;
		case FM2:
			resid = R.drawable.fm2;
			LOCImageButton.setEnabled(true);
			break;
		case FM3:
			resid = R.drawable.fm3;
			LOCImageButton.setEnabled(true);
			break;
		case AM1:
			resid = R.drawable.am1;
			LOCImageButton.setEnabled(false);
			break;
		case AM2:
			resid = R.drawable.am2;
			LOCImageButton.setEnabled(false);
			break;
		default:
			resid = R.drawable.fm1;
			break;
		}

		mRadioBandTypeTextView.setBackgroundResource(resid);
	}

	protected void SetSaveFreqText(int index, String curFreqStr) {
		switch (index) {
		case 0x01:
			mRadioChannelSave1.setText(curFreqStr);
			break;
		case 0x02:
			mRadioChannelSave2.setText(curFreqStr);
			break;
		case 0x03:
			mRadioChannelSave3.setText(curFreqStr);
			break;
		case 0x04:
			mRadioChannelSave4.setText(curFreqStr);
			break;
		case 0x05:
			mRadioChannelSave5.setText(curFreqStr);
			break;
		case 0x06:
			mRadioChannelSave6.setText(curFreqStr);
			break;
		default:
			break;
		}
	}

	/*
	 * ������Ч����
	 */
	private void startAudioSettings() {
		bAudioSet = true;

		Intent i = new Intent();
		i.setClassName("com.android.settings",
				"com.android.settings.apical.AudioSettings");
		startActivity(i);
	}

	/*
	 * qulingling 20130827 将保存的频点转换为字符串,留着后面显示
	 */
	protected void SetFreqToString() {
		Log.d(TAG, "SetAllFreqList------------>0");

		freqValueString = new String[nTotalPage * 6];
		if ((mRadioBandType <= FM3) && (mRadioBandType >= FM1)) {
			for (int i = 0; i < nTotalPage * num_one_page; i++) {
				freqValueString[i] = changeFreq2String(mFreqNum[i]);
			}
		} else {
			for (int i = 0; i < nTotalPage * num_one_page; i++) {
				freqValueString[i] = String.valueOf(mFreqNum[i]);
			}
		}

		SetListFreq();
	}

	/*
	 * qulingling 20130827 根据不同的页数设置不同的频点
	 */
	protected void SetListFreq() {
		// 设置保存的频率
		for (int i = 0; i < num_one_page; i++) {
			switch (i) {
			case 0:
				mRadioChannelSave1.setText(freqValueString[nCurPage
						* num_one_page]);
				break;
			case 1:
				mRadioChannelSave2.setText(freqValueString[nCurPage
						* num_one_page + 1]);
				break;
			case 2:
				mRadioChannelSave3.setText(freqValueString[nCurPage
						* num_one_page + 2]);
				break;
			case 3:
				mRadioChannelSave4.setText(freqValueString[nCurPage
						* num_one_page + 3]);
				break;
			case 4:
				mRadioChannelSave5.setText(freqValueString[nCurPage
						* num_one_page + 4]);
				break;
			case 5:
				mRadioChannelSave6.setText(freqValueString[nCurPage
						* num_one_page + 5]);
				break;
			}
		}
	}

	/*
	 * qulingling 20130827 设置保存频率当前显示的页数
	 */
	protected void ListFreqPage(int nAddPage) {
		Log.d(TAG, "QLLRadio ListFreqPage nAddPage=" + nAddPage);

		// 计算页数
		nCurPage = nCurPage + nAddPage;
		if (nCurPage < 0) {
			nCurPage = 0;
		} else if (nCurPage >= nTotalPage) {
			nCurPage = nTotalPage - 1;
		}
		Log.d(TAG, "QLLRadio ListFreqPage nPage=" + nCurPage);

		SetListFreq();
	}

	/*
	 * qulingling 20130827 长按下保存频率
	 */
	protected void SaveCurFreq(int nButton) {
		byte channel = (byte) ((RadioController.RADIO_SELECT_CHANNEL_BEGIN) + nButton);
		mRadioController.SaveRadioFreq(channel);
	}

	/*
	 * qulingling 20130827 播放选中的频率
	 */
	protected void PlayListRadio(int nButton) {
		Log.d(TAG, "QLLRadio nTotalPage = " + nTotalPage + "  nCurPage="
				+ nCurPage + " nButton" + nButton);
		if ((nTotalPage - 1) == nCurPage) {
			int remainder = Freq_len / num_one_page;
			int nSelButton = nButton + nCurPage * num_one_page;
			Log.d(TAG, "QLLRadio remainder = " + remainder + "  nSelButton="
					+ nSelButton);
			if ((remainder > 0) && (nSelButton >= Freq_len)) {
				int j = nCurPage * num_one_page;
				for (int i = Freq_len; i < nTotalPage * num_one_page; i++, j++) {
					if (j >= Freq_len) {
						j = nCurPage * num_one_page;
					}

					if (nSelButton == i) {
						Log.d(TAG, "QLLRadio i = " + i + "  j=" + j);
						nButton = j % num_one_page;
						break;
					}
				}

			}
		}
		Log.d(TAG, "QLLRadio nButton = " + nButton);

		byte channel = (byte) ((RadioController.RADIO_SELECT_CHANNEL_BEGIN)
				+ nButton + nCurPage * num_one_page);
		mRadioController.PlayRadioInList(channel);
	}

	/*
	 * qulingling 20130828 显示信号量
	 */
	protected void DispSignal(byte nSignal) {
		if (nSignal > 85) {
			mImageSignal.setBackgroundResource(R.drawable.signal_5);
		} else if (nSignal > 75) {
			mImageSignal.setBackgroundResource(R.drawable.signal_4);
		} else if (nSignal > 55) {
			mImageSignal.setBackgroundResource(R.drawable.signal_3);
		} else if (nSignal > 40) {
			mImageSignal.setBackgroundResource(R.drawable.signal_2);
		} else if (nSignal > 20) {
			mImageSignal.setBackgroundResource(R.drawable.signal_1);
		} else {
			mImageSignal.setBackgroundResource(R.drawable.signal_0);
		}
	}

	/*
	 * qulingling 20130830 停止第3方音乐播放
	 */
	protected void StopMusic() {
		Intent stopMusicIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		KeyEvent MusicKeyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_MEDIA_STOP);
		stopMusicIntent.putExtra(Intent.EXTRA_KEY_EVENT, MusicKeyEvent);
		this.sendBroadcast(stopMusicIntent);
	}

	// ��ȡ�洢��Ƶ���б�
	protected void onEvent_RET_FreqList(Bundle data) {
		Log.d(TAG, "onEvent_RET_FreqList() data = " + data);

		Byte bandType = data.getByte("Band");
		SetRadioBand(bandType);
		Freq_len = data.getShort("paramlen");
		Freq_len = (Freq_len - 1) / 2;

		// 将显示页数复位
		nCurPage = 0;
		nTotalPage = Freq_len / num_one_page;
		int nRemainder = Freq_len % num_one_page;
		if (nRemainder > 0) {
			nTotalPage = nTotalPage + 1;
		}
		mFreqNum = new short[nTotalPage * num_one_page];
		for (int i = 0; i < Freq_len; i++) {
			short defaultFreqNum = 0;
			mFreqNum[i] = data.getShort("Freq" + i, defaultFreqNum);

			Log.d(TAG, "QLLRadio mFreqNum[" + i + "]=" + mFreqNum[i]
					+ "  Freq_len=" + Freq_len);
		}
		// 将最后一页的补全
		int j = (nTotalPage - 1) * num_one_page;
		for (int i = Freq_len; i < nTotalPage * num_one_page; i++, j++) {
			if (j >= Freq_len) {
				j = (nTotalPage - 1) * num_one_page;
			}
			short defaultFreqNum = 0;
			mFreqNum[i] = data.getShort("Freq" + j, defaultFreqNum);

			Log.d(TAG, "QLLRadio mFreqNum[" + i + "]=" + mFreqNum[i]
					+ "  Freq_len=" + Freq_len);
		}

		SetFreqToString();
	}

	// ��ȡ��ǰƵ��
	protected void onEvent_RET_Current_Freq(Bundle data) {
		Log.d(TAG, "onEvent_RET_Current_Freq----->data = " + data);

		Byte bandType = data.getByte("Band");
		SetRadioBand(bandType);
		mCurFreqIndex = data.getByte("Pos");
		mCurFreq = data.getShort("Freq");

		if (mMBType == DvdMBType.MB_05DVD || mMBType == DvdMBType.MB_05HDVD) {
			// qulingling
			Byte mRssi = 0;
			mRssi = data.getByte("Rssi");
			Log.d(TAG, "*************************qulingling 2********" + mRssi);
			DispSignal(mRssi);
			// mSignalTextView.setText(""+mRssi);
		}

		if ((bandType <= FM3) && (bandType >= FM1)) {
			mCurFreqString = changeFreq2String(mCurFreq);

			if (mMBType == DvdMBType.MB_XUGANG) {
				MIN_CHANNEL = 8750;
				MAX_CHANNEL = 10800;

				Log.d(TAG,
						"*************************qulingling 2********MIN_CHANNEL="
								+ MIN_CHANNEL + " MAX_CHANNEL=" + MAX_CHANNEL);
			} else {
				String minString = changeFreq2String(shortMin);
				String maxString = changeFreq2String(shortMax);
				MIN_CHANNEL = changeFreq2Short(shortMin);
				MAX_CHANNEL = changeFreq2Short(shortMax);

				Log.d(TAG,
						"*************************qulingling 2********MIN_CHANNEL="
								+ MIN_CHANNEL + " MAX_CHANNEL=" + MAX_CHANNEL);
			}
			int left = mSeekBar.getPaddingLeft();
			int right = mSeekBar.getPaddingRight();
			int top = mSeekBar.getPaddingTop();
			int bottom = mSeekBar.getPaddingBottom();
			mSeekBar.setBackgroundResource(R.drawable.fm_channel_bg);
			mSeekBar.setPadding(left, top, right, bottom);
			mSeekBar.setMax(MAX_CHANNEL - MIN_CHANNEL);// FM
			mRaidoUnit.setBackgroundResource(R.drawable.mhz);
			mSeekBar.setProgress(FMFreq2Int(mCurFreq) - MIN_CHANNEL);
		} else {
			mCurFreqString = String.valueOf(mCurFreq);

			if (mMBType == DvdMBType.MB_XUGANG) {
				MIN_CHANNEL = 531;
				MAX_CHANNEL = 1629;

				Log.d(TAG,
						"*************************qulingling 2********MIN_CHANNEL="
								+ MIN_CHANNEL + " MAX_CHANNEL=" + MAX_CHANNEL);
			} else {
				String minString = shortMin + "";
				String maxString = shortMax + "";
				MIN_CHANNEL = shortMin;
				MAX_CHANNEL = shortMax;

			}
			int left = mSeekBar.getPaddingLeft();
			int right = mSeekBar.getPaddingRight();
			int top = mSeekBar.getPaddingTop();
			int bottom = mSeekBar.getPaddingBottom();
			mSeekBar.setBackgroundResource(R.drawable.am_channel_bg);
			mSeekBar.setPadding(left, top, right, bottom);
			mSeekBar.setMax(MAX_CHANNEL - MIN_CHANNEL);// AM
			mRaidoUnit.setBackgroundResource(R.drawable.khz);
			mSeekBar.setProgress(mCurFreq - MIN_CHANNEL);
		}

		SetSaveFreqText(mCurFreqIndex, mCurFreqString);
		mChannelNumShowTextView.setBackgroundDrawable(new BitmapDrawable(picNum
				.getStringPic(mCurFreqString)));
	}

	protected void onEvent_RET_RADIO_STATE(Bundle data) {
		Log.d(TAG, "onEvent_RET_RADIO_STATE----->data = " + data);
		byte state1 = data.getByte("Status1");
		byte state2 = data.getByte("Status2");
		byte state3 = data.getByte("Status3");
		String radioStateString = "";
		String radioFlagString = "";
		String radioTAString = "";
		String radioTPString = "";
		String radioAFString = "";

		if (mMBType == DvdMBType.MB_05DVD || mMBType == DvdMBType.MB_05HDVD) {
			// qulingling
			Byte mRssi = 0;
			mRssi = data.getByte("Rssi");
			Log.d(TAG, "*************************qulingling 2********" + mRssi);
			DispSignal(mRssi);
		}

		if ((state1 & STATUS1_SEEK_AS) == STATUS1_SEEK_AS) {
			radioFlagString = "AS";
		}
		if ((state1 & STATUS1_SEEK_PS) == STATUS1_SEEK_PS) {
			radioFlagString = "PS";
		}
		if ((state1 & STATUS1_SEEK_AF) == STATUS1_SEEK_AF) {
			bAFMode = true;
		} else {
			bAFMode = false;
		}
		if ((state1 & STATUS1_SEEK_TA) == STATUS1_SEEK_TA) {
			radioTAString = "TA";
		}
		if ((state1 & STATUS1_SEEK_RDS) == STATUS1_SEEK_RDS) {
			bRDSSignal = true;
		} else {
			bRDSSignal = false;
		}

		// ״̬2���
		if ((state2 & STATUS2_SEEK_LOC) == STATUS2_SEEK_LOC) {
			// qulingling 20140106
			// radioStateString = "LOC";
			LOCImageButton.setBackgroundResource(R.drawable.loc_btn_sel);
		} else {
			LOCImageButton.setBackgroundResource(R.drawable.loc_btn);
		}

		if ((state2 & STATUS2_SEEK_SCAN) == STATUS2_SEEK_SCAN) {
			radioStateString = "SCAN";
		}
		if ((state2 & STATUS2_SEEK_TMC) == STATUS2_SEEK_TMC) {
			radioTPString = "TP";
		}
		if ((state2 & STATUS2_SEEK_ST) == STATUS2_SEEK_ST) {
			radioStateString = "ST";
		}
		if ((state2 & STATUS2_SEEK_MONO) == STATUS2_SEEK_MONO) {
			radioStateString = "MONO";
		}

		mRadioState.setText(radioStateString);
		mRadioFlag.setText(radioFlagString);
		mTAStatus.setText(radioTAString);
		mTPStatus.setText(radioTPString);
	}

	protected void onEvent_RET_WORKMODE(Bundle data) {
		Log.d(TAG, "onEvent_RET_WORKMODE---->data = " + data);

	}

	/*
	 * qulingling 20131023 get the radio station
	 */
	protected void onEvent_RET_RADIO_STATION(Bundle data) {
		short stationLen = data.getShort("paramlen"); // �����
		byte[] stationInfo = new byte[stationLen - 1];
		stationInfo = data.getByteArray("cName");

		String stationName = "";
		try {
			stationName = new String(stationInfo, "ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mRadioStation.setText(stationName);
	}

	/*
	 * qulingling 20131023 get the pty status
	 */
	protected void onEvent_RET_PTY_STATUS(Bundle data) {
		Byte bytePtyStatus = 0;
		bytePtyStatus = data.getByte("PTYStatus");

		switch (bytePtyStatus) {
		case 0:
			mPtyStatus.setText("NO PTY");
			break;
		case 1:
			mPtyStatus.setText("NEWS");
			break;
		case 2:
			mPtyStatus.setText("AFFAIRS");
			break;
		case 3:
			mPtyStatus.setText("INFO");
			break;
		case 4:
			mPtyStatus.setText("SPORT");
			break;
		case 5:
			mPtyStatus.setText("EDUCATE");
			break;
		case 6:
			mPtyStatus.setText("DRAMA");
			break;
		case 7:
			mPtyStatus.setText("CULTURE");
			break;
		case 8:
			mPtyStatus.setText("SCIENCE");
			break;
		case 9:
			mPtyStatus.setText("VARIED");
			break;
		case 10:
			mPtyStatus.setText("POP M");
			break;
		case 11:
			mPtyStatus.setText("ROCK M");
			break;
		case 12:
			mPtyStatus.setText("EASY M");
			break;
		case 13:
			mPtyStatus.setText("LIGHT M");
			break;
		case 14:
			mPtyStatus.setText("CLASSICS");
			break;
		case 15:
			mPtyStatus.setText("OTHER M");
			break;
		case 16:
			mPtyStatus.setText("WEATHER");
			break;
		case 17:
			mPtyStatus.setText("FINANCE");
			break;
		case 18:
			mPtyStatus.setText("CHILDREN");
			break;
		case 19:
			mPtyStatus.setText("SOCIAL");
			break;
		case 20:
			mPtyStatus.setText("RELIGION");
			break;
		case 21:
			mPtyStatus.setText("PHONE IN");
			break;
		case 22:
			mPtyStatus.setText("TRAVEL");
			break;
		case 23:
			mPtyStatus.setText("LEISURE");
			break;
		case 24:
			mPtyStatus.setText("JAZZ");
			break;
		case 25:
			mPtyStatus.setText("COUNTRY");
			break;
		case 26:
			mPtyStatus.setText("NATION M");
			break;
		case 27:
			mPtyStatus.setText("OLDIES");
			break;
		case 28:
			mPtyStatus.setText("FOLK M");
			break;
		case 29:
			mPtyStatus.setText("DOCUMENT");
			break;
		case 30:
			mPtyStatus.setText("TEST");
			break;
		case 31:
			mPtyStatus.setText("ALARM!");
			break;
		case 32:
			mPtyStatus.setText("Nothing!");
			break;
		default:
			mPtyStatus.setText(" ");
			break;
		}
	}

	/*
	 * qulingling 20131105 get
	 */
	protected void onEvent_RET_MCU_STEP(Bundle data) {
		Byte byteMinL = data.getByte("MinL");
		Byte byteMinH = data.getByte("MinH");
		Byte byteMaxL = data.getByte("MaxL");
		Byte byteMaxH = data.getByte("MaxH");

		shortMin = bytesToShort(byteMinL, byteMinH);
		shortMax = bytesToShort(byteMaxL, byteMaxH);

		if ((mMBType == DvdMBType.MB_05DVD) || (mMBType == DvdMBType.MB_05HDVD)) {
			Byte bandType = data.getByte("Band");
			if ((bandType <= FM3) && (bandType >= FM1)) {
				String minString = changeFreq2String(shortMin);
				String maxString = changeFreq2String(shortMax);
				MIN_CHANNEL = changeFreq2Short(shortMin);
				MAX_CHANNEL = changeFreq2Short(shortMax);

				Log.d(TAG,
						"*************************qulingling 2********MIN_CHANNEL="
								+ MIN_CHANNEL + " MAX_CHANNEL=" + MAX_CHANNEL);

				mSeekBar.setMax(MAX_CHANNEL - MIN_CHANNEL);// FM
				mRaidoUnit.setBackgroundResource(R.drawable.mhz);
				mSeekBar.setProgress(FMFreq2Int(mCurFreq) - MIN_CHANNEL);
			} else {
				String minString = shortMin + "";
				String maxString = shortMax + "";
				MIN_CHANNEL = shortMin;
				MAX_CHANNEL = shortMax;

				mSeekBar.setMax(MAX_CHANNEL - MIN_CHANNEL);// AM
				mRaidoUnit.setBackgroundResource(R.drawable.khz);
				mSeekBar.setProgress(mCurFreq - MIN_CHANNEL);
			}

		}
		Log.d(TAG, "*************************qulingling 2********shortMin="
				+ shortMin + " shortMax=" + shortMax);
	}

	/*
	 * qulingling 20131223 get
	 */
	protected void onEvent_RET_RDS_EN(Bundle data) {
		RdsDisp();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// �˳�radio����
			mRadioController.CloseRadio();
			// �ͷ���Դ
			mApicalHawreCtrl.AudioSourceRelease();
			finish();
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		// pre
		else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
			Log.d(TAG, "onKeyDown preTrackImageButton");
			mRadioController.RadioCtrl(RadioController.RADIO_HANDLE_SEARCH_UP);
		}
		// next
		else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
			mRadioController
					.RadioCtrl(RadioController.RADIO_HANDLE_SEARCH_DOWN);
		}
		// fast reward /next
		else if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
			mRadioController.RadioCtrl(RadioController.RADIO_AUTO_SEARCH_DOWN);
		}
		// fast baCK
		else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
			mRadioController.RadioCtrl(RadioController.RADIO_AUTO_SEARCH_UP);
		}

		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	private short bytesToShort(byte bLow, byte bHigh) {
		short s;
		s = (short) bLow;
		if (s < 0)
			s += 256;
		s |= (short) (bHigh << 8);

		return s;
	}

}
