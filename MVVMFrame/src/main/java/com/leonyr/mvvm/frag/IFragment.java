package com.leonyr.mvvm.frag;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.leonyr.lib.utils.LogUtil;
import com.leonyr.mvvm.R;
import com.leonyr.mvvm.act.Common;

public class IFragment extends Fragment {

    public void openFragment(Common.Type type) {
        openFragment(type.newFragment(), type.getTag());
    }

    public void openFragment(Fragment fragment, String tag) {

        Fragment targetParent = this;
        while (targetParent.getId() != R.id.fragment_container) {
            targetParent = targetParent.getParentFragment();
            LogUtil.e(getClass().getSimpleName(), "id: " + targetParent.getId());
        }

        FragmentManager fm = targetParent.getFragmentManager();
        fm.beginTransaction()
                .hide(targetParent)
                .add(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    protected void setResult(int result_OK, Intent intent) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            return;
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), result_OK, intent);
        getFragmentManager().popBackStack();
    }

}
