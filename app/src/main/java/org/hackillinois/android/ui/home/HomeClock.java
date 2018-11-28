//package org.hackillinois.android.ui.home;
//
//import android.os.CountDownTimer;
//
//import com.airbnb.lottie.LottieAnimationView;
//import com.annimon.stream.Collectors;
//import com.annimon.stream.Stream;
//
//import org.joda.time.DateTime;
//import org.joda.time.Period;
//
//import java.util.List;
//
//import timber.log.Timber;
//
//public class HomeClock {
//
//    public interface OnFinishListener {
//
//        void onFinish(int timerIndex);
//    }
//
//    private CountDownTimer timer;
//    private static final float SECONDS_SPEED_RATIO = 2f;//1f / (2.0f);
//
//    private OnFinishListener onFinishListener;
//
//    private List<DateTime> activeTimers;
//    private int finishedTimers;
//    private final LottieAnimationView secondAnimation;
//
//    private final LottieAnimationView minuteAnimation;
//    private final LottieAnimationView hourAnimation;
//    private final LottieAnimationView daysAnimation;
//
//    public HomeClock(
//            LottieAnimationView secondAnimation,
//            LottieAnimationView minuteAnimation,
//            LottieAnimationView hourAnimation,
//            LottieAnimationView daysAnimation
//    ) {
//        this.secondAnimation = secondAnimation;
//        this.minuteAnimation = minuteAnimation;
//        this.hourAnimation = hourAnimation;
//        this.daysAnimation = daysAnimation;
//
//        initializeSpeeds();
//    }
//
//    private void initializeSpeeds() {
//        secondAnimation.setSpeed(-SECONDS_SPEED_RATIO);
//        minuteAnimation.setSpeed(-SECONDS_SPEED_RATIO);
//        hourAnimation.setSpeed(-SECONDS_SPEED_RATIO);
//        daysAnimation.setSpeed(-SECONDS_SPEED_RATIO);
//    }
//
//    /**
//     * The input list of dates is assumed to be sorted
//     *
//     * @param onFinishListener
//     * @param dateTimes
//     */
//    public void setCountDownTo(OnFinishListener onFinishListener, List<DateTime> dateTimes) {
//        this.onFinishListener = onFinishListener;
//        this.activeTimers = Stream.of(dateTimes).filter(DateTime::isAfterNow).collect(Collectors.toList());
//
//        finishedTimers = dateTimes.size() - activeTimers.size();
//        if (onFinishListener != null) {
//            onFinishListener.onFinish(finishedTimers);
//        }
//
//        playNext();
//    }
//
//    public void onPause() {
//        if (timer != null) {
//            timer.cancel();
//        }
//    }
//
//    public void onResume() {
//        secondAnimation.invalidate();
//        minuteAnimation.invalidate();
//        hourAnimation.invalidate();
//        daysAnimation.invalidate();
//        Period diff;
//        if (activeTimers.size() > 0) {
//            diff = new Period(DateTime.now(), activeTimers.get(0));
//        } else {
//            diff = Period.ZERO;
//        }
//        setAllFrames(diff);
//        if (timer != null) {
//            timer.start();
//        }
//    }
//
//    private void playNext() {
//        if (activeTimers.size() > 0) {
//            DateTime nextTimer = activeTimers.get(0);
//            Timber.d("Going to next active timer %s", nextTimer);
//            setCountdownView(nextTimer);
//        } else {
//            setAllFrames(Period.ZERO);
//        }
//    }
//
//    private void setCountdownView(DateTime time) {
//        if (time.isBeforeNow()) {
//            return;
//        }
//
//        setAllFrames(new Period(DateTime.now(), time));
//
//        long millisUntilFinish = time.getMillis() - DateTime.now().getMillis();
//        timer = new CountDownTimer(millisUntilFinish, 500) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Period diff = new Period(DateTime.now(), time);
//                int seconds = diff.getSeconds();
//                int minutes = diff.getMinutes();
//                int hours = diff.getHours();
//                int days = diff.getDays() + 7 * diff.getWeeks();
//                tickSecond(seconds, minutes, hours, days);
//            }
//
//            @Override
//            public void onFinish() {
//                finishCurrentTimer();
//                cancel();
//                timer = null; // nothing can happen until a new timer is made
//            }
//        }.start();
//    }
//
//    private void finishCurrentTimer() {
//        Timber.d("Finished current timer");
//        if (onFinishListener != null) {
//            ++finishedTimers;
//            onFinishListener.onFinish(finishedTimers);
//        }
//        if (activeTimers.size() > 0) {
//            activeTimers.remove(0);
//            playNext();
//        }
//    }
//
//    private void setAllFrames(Period diff) {
//        secondAnimation.setFrame(numberToFrame(diff.getSeconds()));
//        minuteAnimation.setFrame(numberToFrame(diff.getMinutes()));
//        hourAnimation.setFrame(numberToFrame(diff.getHours()));
//        daysAnimation.setFrame(numberToFrame(diff.getDays() + 7 * diff.getWeeks()));
//    }
//
//    private int lastSecond = 0;
//
//    private void tickSecond(int seconds, int minutes, int hours, int days) {
//        if (lastSecond == seconds) { // because the timer doesn't assure this
//            return;
//        }
//
//        if (seconds == 0) {
//            tickMinute(seconds, minutes, hours, days);
//            secondAnimation.setFrame(numberToFrame(60));
//            if (minutes > 0 || hours > 0 || days > 0) {
//                secondAnimation.setMinAndMaxFrame(numberToFrame(59), numberToFrame(60));
//                secondAnimation.resumeAnimation();
//            }
//        } else {
//            secondAnimation.setFrame(numberToFrame(seconds));
//            secondAnimation.setMinAndMaxFrame(numberToFrame(seconds - 1), numberToFrame(seconds));
//            secondAnimation.resumeAnimation();
//        }
//        lastSecond = seconds;
//    }
//
//    private void tickMinute(int seconds, int minutes, int hours, int days) {
//        if (minutes == 0) {
//            tickHour(seconds, minutes, hours, days);
//            minuteAnimation.setFrame(numberToFrame(60));
//            if (hours > 0 || days > 0) {
//                minuteAnimation.setMinAndMaxFrame(numberToFrame(59), numberToFrame(60));
//                minuteAnimation.resumeAnimation();
//            }
//        } else {
//            minuteAnimation.setFrame(numberToFrame(minutes));
//            minuteAnimation.setMinAndMaxFrame(numberToFrame(minutes - 1), numberToFrame(minutes));
//            minuteAnimation.resumeAnimation();
//        }
//    }
//
//    private void tickHour(int seconds, int minutes, int hours, int days) {
//        if (hours == 0) {
//            tickDay(seconds, minutes, hours, days);
//            hourAnimation.setFrame(numberToFrame(24));
//            if (days > 0) {
//                hourAnimation.setMinAndMaxFrame(numberToFrame(23), numberToFrame(24));
//                hourAnimation.resumeAnimation();
//            }
//        } else {
//            hourAnimation.setFrame(numberToFrame(hours));
//            hourAnimation.setMinAndMaxFrame(numberToFrame(hours - 1), numberToFrame(hours));
//            hourAnimation.resumeAnimation();
//        }
//    }
//
//    private void tickDay(int seconds, int minutes, int hours, int days) {
//        if (days == 0) {
//            daysAnimation.setFrame(numberToFrame(60));
//        } else {
//            daysAnimation.setFrame(numberToFrame(days));
//            daysAnimation.setMinAndMaxFrame(numberToFrame(days - 1), numberToFrame(days));
//            daysAnimation.resumeAnimation();
//        }
//    }
//
//    public static int numberToFrame(int displayNumber) {
//        return 30 * displayNumber;
//    }
//}
//
