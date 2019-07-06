package com.glens.jksd.fragment.RepairTaskFragments.standtask;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glens.jksd.R;
import com.glens.jksd.activity.activity_repair.stand_detail.RepairDefectDetailActivity;
import com.glens.jksd.adapter.RepairTaskAdapter.RepairStandardTaskAdapter;
import com.glens.jksd.base.BaseFragment;
import com.glens.jksd.bean.RepairTowerListBean;
import com.glens.jksd.network.presenter.RepairTowerCheckPresenter;
import com.glens.jksd.network.view.RepairTowerCheckView;
import com.glens.jksd.utils.PreferenceUtils;
import com.glens.jksd.utils.RepairConstantUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消缺工作
 */
public class AddDefectFragment extends BaseFragment implements RepairTowerCheckView {
    public static final String TAG = AddDefectFragment.class.getSimpleName();
    @Bind(R.id.repair_ground_RecyclerView)
    RecyclerView mRepairGroundRecyclerView;
    @Bind(R.id.repair_task_standard_text)
    TextView mRepairTaskStandardText;
    @Bind(R.id.repair_task_standard_ry)
    RelativeLayout mRepairTaskStandardRy;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.tv_standard_start_time)
    TextView mTvStandardStartTime;
    @Bind(R.id.tv_standard_end_time)
    TextView mTvStandardEndTime;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.ry_bottom)
    RelativeLayout ryBottom;


    private RepairStandardTaskAdapter mRepairStandardTaskAdapter;
    private List<RepairTowerListBean.RecordsBean> mTaskList;
    private RepairTowerCheckPresenter mPresenter;
    private String mTaskCode;
    private RepairTowerListBean mRepairListBean;


    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_repair_ground, null);
        ButterKnife.bind(this, view);
        ryBottom.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        initNetData();
        initRepairStandardView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

    /**
     * 初始化绑定presenter
     */
    private void initNetData() {
        mTaskList = new ArrayList<>();
        mTaskCode = PreferenceUtils.getString(getActivity(), "taskCode", "");
        mRepairStandardTaskAdapter = new RepairStandardTaskAdapter(mTaskList, getActivity());
        mPresenter = new RepairTowerCheckPresenter(getActivity());
        mPresenter.onCreate();
        mPresenter.BindPresentView(this);
        mPresenter.getRepairTaskList("", "", mTaskCode);
    }

    /**
     * 初始化 Recycler配置
     */
    private void initRepairStandardView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRepairGroundRecyclerView.setLayoutManager(linearLayoutManager);
        mRepairStandardTaskAdapter.setOnItemClickListener((v, bean) -> {
            RepairTowerListBean.RecordsBean recordsBean = (RepairTowerListBean.RecordsBean) bean;
            changeToRepairTaskItemActivity(recordsBean.getRecordCode());
        });
    }

    private void changeToRepairTaskItemActivity(String checkRecordCode) {
        Intent intent = new Intent(getActivity(), RepairDefectDetailActivity.class);
        intent.putExtra(RepairConstantUtils.TASKCODE, checkRecordCode);
        startActivity(intent);
    }

    @Override
    public void onSuccess(RepairTowerListBean bean, String resultMsg) {
        mRepairListBean = bean;
        if (mRepairStandardTaskAdapter != null && bean.getRecords() != null) {
            mRepairGroundRecyclerView.setAdapter(mRepairStandardTaskAdapter);
            mRepairStandardTaskAdapter.setData(bean.getRecords());
        }
    }

    @Override
    public void setTimeTaskSearch(String startTime, String endTime) {
        mPresenter.getRepairTower(startTime, endTime, mTaskCode);
    }

    @Override
    public void showDialog(String message) {
        showSvpDilog(getActivity(), message, false, null, null);
    }

    @Override
    public void hideDialog() {
        dismissSvpDialog(RepairConstantUtils.DIALOG_TIME);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(String result) {
        Log.e(TAG, "请求结果 " + result);
    }

    @OnClick({R.id.tv_standard_start_time, R.id.tv_standard_end_time, R.id.repair_task_standard_ry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_standard_start_time:
                mPresenter.openTimerPicker(RepairConstantUtils.START_TIME, getActivity());
                break;
            case R.id.tv_standard_end_time:
                mPresenter.openTimerPicker(RepairConstantUtils.END_TIME, getActivity());
                break;
            case R.id.repair_task_standard_ry:
                changeToTowerAdd();
                break;
        }
    }

    private void changeToTowerAdd() {

    }


}
