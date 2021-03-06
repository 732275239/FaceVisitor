package com.chuyu.face.permissions.easy;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * {@link AppCompatDialogFragment} to display rationale for permission requests when the request comes from
 * a Fragment or Activity that can host a Fragment.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RationaleDialogFragmentCompat extends AppCompatDialogFragment {
    
    private EasyPermissions.PermissionCallbacks permissionCallbacks;
    private RationaleDialogConfig config;
    private RationaleDialogClickListener clickListener;

    static RationaleDialogFragmentCompat newInstance(
            @StringRes int positiveButton, @StringRes int negativeButton,
            @NonNull String rationaleMsg, int requestCode, @NonNull String[] permissions) {

        // Create new Fragment
        RationaleDialogFragmentCompat dialogFragment = new RationaleDialogFragmentCompat();

        // Initialize configuration as arguments
        RationaleDialogConfig config = new RationaleDialogConfig(
                positiveButton, negativeButton, rationaleMsg, requestCode, permissions);
        dialogFragment.setArguments(config.toBundle());

        return dialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null && getParentFragment() instanceof EasyPermissions.PermissionCallbacks) {
            permissionCallbacks = (EasyPermissions.PermissionCallbacks) getParentFragment();
        } else if (context instanceof EasyPermissions.PermissionCallbacks) {
            permissionCallbacks = (EasyPermissions.PermissionCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        permissionCallbacks = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Rationale dialog should not be cancelable
        setCancelable(false);

        // Get config from arguments, create click listener
        config = new RationaleDialogConfig(getArguments());
        clickListener = new RationaleDialogClickListener(this, config, permissionCallbacks);

        // Create an AlertDialog
        return config.createDialog(getContext(), clickListener);
    }
}
