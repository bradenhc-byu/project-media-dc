package com.byu.pmedia.tasks;

public interface StillFaceTaskCallback {

    /**
     * When the JavaFX Task has completed, this method will be called when a StillFaceTaskCallback is passed to
     * the appropriate StillFaceTask object.
     */
    void onSuccess();

    /**
     * When the JavaFX Task fails (i.e. and exception is caught from inside), this method will be called and the
     * task will terminate.
     * @param exception The exception caught by the task
     */
    void onFail(Throwable exception);

}
