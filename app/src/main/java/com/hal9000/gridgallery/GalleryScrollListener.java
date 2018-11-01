package com.hal9000.gridgallery;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;

public class GalleryScrollListener implements AbsListView.OnScrollListener {
    /** Scroll listener that aligns GridView contents to the top depending on how much of the topmost element on screen is visible
     *  (less than half => animate to next row, more than half => animate align top of the top row to the top of the screen).
     *  If you don't want animation remove ObjectAnimator and simply scroll GridView using smoothScrollToPosition() or setSelection().
     * **/

    private static final int SCROLL_ANIMATION_DUR = 500;
    private int currentScrollState;
    private int currentFirstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private boolean draggedFlag = false;
    private boolean userInputFlag = false;
    private GridView gridView;
    private ObjectAnimator animateScroll = null;

    public GalleryScrollListener(GridView gridView){
        this.gridView = gridView;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;

        if (animateScroll != null){
            animateScroll.cancel();
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void isScrollCompleted() {


        if (this.currentScrollState == SCROLL_STATE_DRAGGING) { // when dragged by outside input such as user touch input
            Log.d("scroll", "isScrollCompleted() DRAGGING");
            draggedFlag = true;
        }
        else if (this.currentScrollState == SCROLL_STATE_IDLE) {
            //this.currentVisibleItemCount > 0 &&
            Log.d("scroll", "isScrollCompleted() IDLE");
            if(draggedFlag) {
                draggedFlag = false;
                // determine how much of the first visible image is cut
                View firstVisiblePic = gridView.getChildAt(0);

                final int imageSizeLeft = firstVisiblePic.getBottom();   // always >0
                final int imageSizeOurOfView = firstVisiblePic.getTop(); // <0 except for transition between images (so while on margins)

                if ( imageSizeLeft > imageSizeOurOfView*(-1) ) {    // scroll up to align to current row
                    animateScroll = ObjectAnimator
                            .ofInt(gridView, "scrollY",  imageSizeOurOfView)
                            .setDuration(SCROLL_ANIMATION_DUR);
                    animateScroll.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) { }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Log.d("scroll", "animation CANCEL");
                        }
                        @Override
                        public void onAnimationRepeat(Animator animation) { }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /** Prevents returning to pre-animation scroll position when user starts scrolling after animation
                             *  (i.e. "saves" animation position) **/
                            gridView.setScrollY(0); // return to default position (from before animation), setSelection() gets messed up without it for some reason
                            gridView.setSelection(currentFirstVisibleItem); // sets actual scroll position to top of picture (where animation ends)
                            Log.d("scroll", "animation END");
                        }
                    });
                    animateScroll.start();

                    Log.d("scroll", "imageSizeLeft = " + Integer.toString(imageSizeLeft));
                    Log.d("scroll", "imageSizeOurOfView*(-1) = " + Integer.toString(imageSizeOurOfView*(-1)));
                }
                else if (gridView.getLastVisiblePosition() + 1 != totalItemCount ) {  // scroll down to align to next row
                    final int itemCount = gridView.getAdapter().getCount();
                    final int columnsCount = gridView.getNumColumns();
                    final int jumpToPos =  itemCount > columnsCount + currentFirstVisibleItem ? columnsCount + currentFirstVisibleItem : itemCount - 1;   // next row

                    animateScroll = ObjectAnimator
                            .ofInt(gridView, "scrollY",  imageSizeLeft + gridView.getVerticalSpacing()) // included spacing to animate to the point where set selection jumps
                            .setDuration(SCROLL_ANIMATION_DUR);
                    animateScroll.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) { }
                        @Override
                        public void onAnimationCancel(Animator animation) { }
                        @Override
                        public void onAnimationRepeat(Animator animation) { }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /** Prevents returning to pre-animation scroll position when user starts scrolling after animation
                             *  (i.e. "saves" animation position) **/
                            gridView.setScrollY(0); // return to default position (from before animation), setSelection() gets messed up without it for some reason
                            gridView.setSelection(jumpToPos); // sets actual scroll position to top of picture (where animation ends)
                            Log.d("scroll", "animation END");
                        }
                    });
                    animateScroll.start();

                    //gridView.setSelection(jumpToPos);
                }
                else{   // scroll down case when last row becomes visible
                    final int itemCount = gridView.getAdapter().getCount();
                    final int columnsCount = gridView.getNumColumns();
                    final int jumpToPos =  itemCount > columnsCount + currentFirstVisibleItem ? columnsCount + currentFirstVisibleItem : itemCount - 1;   // next row

                    View lastVisiblePic = gridView.getChildAt(visibleItemCount-1);
                    final int lastImageSizeLeftToTheEnd = firstVisiblePic.getBottom();   // always >0

                    animateScroll = ObjectAnimator
                            .ofInt(gridView, "scrollY",lastImageSizeLeftToTheEnd-6) // essentially: scroll to the end; -6 because it was moving to far by that much (via debugging)
                            .setDuration(SCROLL_ANIMATION_DUR);
                    animateScroll.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) { }
                        @Override
                        public void onAnimationCancel(Animator animation) { }
                        @Override
                        public void onAnimationRepeat(Animator animation) { }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /** Prevents returning to pre-animation scroll position when user starts scrolling after animation
                             *  (i.e. "saves" animation position) **/
                            gridView.setScrollY(0); // return to default position (from before animation), setSelection() gets messed up without it for some reason
                            gridView.setSelection(jumpToPos); // sets actual scroll position to top of picture (where animation ends)
                            Log.d("scroll", "animation END");
                        }
                    });
                    animateScroll.start();

                    //gridView.setSelection(jumpToPos);
                }
            }
        }
    }

    private void getTopRowVisibilityParams(){
        //Log.d("scroll", "gridView.getBottom() = " + Integer.toString(gridView.getBottom()));
        //Log.d("scroll", "gridView.getTop() = " + Integer.toString(gridView.getTop()));
        View v = gridView.getChildAt(0);
        Log.d("scroll", "v.getBottom() = " + Integer.toString(v.getBottom()));
        Log.d("scroll", "v.getTop() = " + Integer.toString(v.getTop()));
    }
}
