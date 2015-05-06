
package micc.theguardiansapp.scrollPager;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import micc.theguardiansapp.dotsProgressBar.DotsProgressBar;

public class MyScrollPager implements OnTouchListener
{
	// The class encapsulates scrolling.(Overshoot)
	private Scroller scroller;
	// The task make scroll view scrolled.
	private Runnable pageScrollTask;
    private Runnable fragmentScrollTask;


    private DotsProgressBar fragmentProgressBar = null;
    private DotsProgressBar pageProgressBar = null;


    private final double treshold_Dy_scrolling = 5; // con una accelerazione di 5 si scrolla
    private final double treshold_y_scrolling_percent = 0.3;//scomprendo il 30% dell'area della pagina successiva si scrolla

	private ScrollView mScrollView;
	private ViewGroup[] mFragmentContainers;
    private ViewGroup mContentView;

    private int[] fragmentFirstPageMap = null; // per ogni fragment dice la posizione della prima pagina di tale fragment
    private int[] fragmentNPageMap = null; // per ogni fragment dice quante pagine sono associate ad esso
    private int[] pageFragmentMap = null; // per ogni pagina dice quale fragment è associato ad essa

    private int nPages;
    private int nFragments;
    private int activeFragment;
    private int lastExactCurrentPage= 0;


    private int currentPage = 0;
    private double last_y = 0;
    private double last_time = 0;
    private double Dy = 0;

    private double first_y = 0;
    private double first_time = 0;
    private double scrollDirection = 0;


    // The height of scroll view, in pixels
    private int displayHeight;
    private boolean stdScrollIfHigher = false;
    private boolean fastScrollJumpFragment = false;


    ScrollPagerListener scrollListener = null;

    private boolean guiInitializated = false;


	public MyScrollPager(ScrollView aScrollView, ViewGroup aContentView, ViewGroup[] aFragmentContainer,
                         ScrollPagerListener listener, boolean stdScrollIfHigher, boolean fastScrollJumpFragment)
	{
		mScrollView = aScrollView;
        mContentView = aContentView;
        mFragmentContainers = aFragmentContainer;
        nFragments = mFragmentContainers.length;
        activeFragment = 0;
        this.stdScrollIfHigher = stdScrollIfHigher;
        this.fastScrollJumpFragment = fastScrollJumpFragment;
        this.scrollListener = listener;


		scroller = new Scroller(mScrollView.getContext(), null);
        pageScrollTask = new Runnable()
		{
			@Override
			public void run()
			{
				scroller.computeScrollOffset();
				mScrollView.scrollTo(0, scroller.getCurrY());
				
				if (!scroller.isFinished())
				{
					mScrollView.post(this);
				}
			}
		};
        fragmentScrollTask = new Runnable()
        {
            @Override
            public void run()
            {

            }
        };


        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                initGUI();
                //FragmentHelper.swapFragment(R.id.fragContainer0, new Fragment0());

                ViewTreeObserver obs = mScrollView.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });


	}

    private void initGUI()
    {
        mScrollView.setSmoothScrollingEnabled(true);
        fragmentFirstPageMap= new int[mFragmentContainers.length];;
        fragmentNPageMap = new int[mFragmentContainers.length];

        mScrollView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        this.displayHeight = mScrollView.getHeight();

        int nPageTotal = 0;
        int i = 0;

        for( ViewGroup v : mFragmentContainers)
        {

            if(v==null) continue;
            // set the heght for each fragment container as a multiple of displayHeight
            double vHeight = v.getHeight();
            double rapporto = vHeight / displayHeight;
            double ceil = Math.ceil(rapporto);

            int nPage = (int)ceil;
            nPageTotal += nPage;
            //int nDisplay = (int) Math.ceil(v.getHeight() / displayHeight);
            v.setMinimumHeight(nPage * displayHeight);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            lp.height = nPage*displayHeight;
            v.setLayoutParams(lp);

            //v.setPadding(0, 100, 0, 100);

            fragmentNPageMap[i] = nPage;
            i++;
        }

        this.nPages = nPageTotal;
        pageFragmentMap = new int[nPageTotal];
        i = 0;
        for(int f = 0; f < fragmentNPageMap.length; f++)
        {
            fragmentFirstPageMap[f] = i;

            for(int p = 0; p < fragmentNPageMap[f]; p++ )
            {
                pageFragmentMap[i] = f;
                i++;
            }
        }



        guiInitializated = true;

        currentPage = 0;
        mScrollView.scrollTo(0, mContentView.getPaddingTop());

        if(pageProgressBar != null) {
            pageProgressBar.setDotsCount(this.nPages);
            pageProgressBar.setActiveDot(0);
            setVerticalScrollBarEnabled(false);
        }
        if(fragmentProgressBar != null) {
            fragmentProgressBar.setDotsCount(this.nFragments);
            fragmentProgressBar.setActiveDot(0);
            setVerticalScrollBarEnabled(false);
        }

        scrollListener.onPagerGuiInit();


    }

    public final void setVerticalScrollBarEnabled(boolean bool) {
        mScrollView.setVerticalScrollBarEnabled(bool);

    }

    public final void setHorizontalScrollBarEnabled(boolean bool) {
        mScrollView.setHorizontalScrollBarEnabled(bool);
    }

    public void setDotsFragmentProgressBar(DotsProgressBar progressBar) {
        this.fragmentProgressBar = progressBar;
        if(guiInitializated) {
            fragmentProgressBar.setDotsCount(this.nFragments);
            fragmentProgressBar.setActiveDot(pageFragmentMap[currentPage]);
            setVerticalScrollBarEnabled(false);
        }
    }
    public void setDotsPageProgressBar(DotsProgressBar progressBar) {
        this.pageProgressBar = progressBar;
        if(guiInitializated) {
            pageProgressBar.setDotsCount(this.nPages);
            pageProgressBar.setActiveDot(currentPage);
            setVerticalScrollBarEnabled(false);
        }
    }


//    public void setOnScrollListener(ScrollPagerListener listener) {
//        this.scrollListener = listener;
//    }


    public int gotoFragment(int fragmentNumber)
    {
        gotoPage(fragmentFirstPageMap[fragmentNumber]);
        return getActiveFragment();
    }
    public int gotoPage(int pageNumber)
    {
        int contentTop = mContentView.getPaddingTop();
        int contentBottom = mContentView.getHeight() - mContentView.getPaddingBottom();
        int lastPageTop = contentBottom - displayHeight;

        int nextPageTop = contentTop + pageNumber * displayHeight;
        int min = Math.min(lastPageTop, nextPageTop);
        int max = Math.max(min, contentTop);
        int currScrollY = mScrollView.getScrollY();
        scroller.startScroll(0, currScrollY, 0, max - currScrollY, 500);// Start scrolling calculation.
        mScrollView.post(pageScrollTask);// Start animation.


        if(currentPage != pageNumber)
        {

            if(pageProgressBar != null)
                pageProgressBar.setActiveDot(pageNumber);
            if(scrollListener != null)
                scrollListener.onPageChanged(currentPage, pageNumber, pageFragmentMap[currentPage], pageFragmentMap[pageNumber] );
            // EVENTO: abbiamo cambiato pagina
            if(pageFragmentMap[currentPage] != pageFragmentMap[pageNumber])
            {
                if(scrollListener != null)
                    scrollListener.onFragmentChanged(pageFragmentMap[currentPage] , pageFragmentMap[pageNumber]);
                // EVENTO: abbiam ocambiato fragment

                if(fragmentProgressBar != null)
                    fragmentProgressBar.setActiveDot(pageFragmentMap[pageNumber]);
            }
        }


        this.currentPage = pageNumber;
        this.activeFragment = pageFragmentMap[pageNumber];

        return currentPage;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// Stop scrolling calculation.
		scroller.forceFinished(true);
		// Stop scrolling animation.
		mScrollView.removeCallbacks(pageScrollTask);




        switch (event.getAction()) {


            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
            {
                Dy = 0;
                last_y = event.getY();
                last_time = event.getEventTime();
                first_y = last_y;
                first_time = last_time;


                int contentTop = mContentView.getPaddingTop();
                int currScrollY = mScrollView.getScrollY();
                int currScrollMiddleY = currScrollY + displayHeight / 2 - contentTop;
                lastExactCurrentPage = currScrollMiddleY / displayHeight;
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                double y = event.getY();
                double time = event.getEventTime();
                Dy = (y - last_y) / (time - last_time);
                last_y = y;
                last_time = time;



                break;
            }


            case MotionEvent.ACTION_UP:
            {
                int beforeTouchPage = currentPage;
                int beforeTouchFragment = pageFragmentMap[currentPage];



                // The top of content view, in pixels.
                //int contentTop = mFragmentContainers[activeFragment].getPaddingTop();
                int contentTop = mContentView.getPaddingTop();


                // The top of content view, in pixels.
                //int contentBottom = mFragmentContainers[activeFragment].getHeight()
                //                        - mFragmentContainers[activeFragment].getPaddingBottom();

                int contentBottom = mContentView.getHeight() - mContentView.getPaddingBottom();


                // The top of last page, in pixels.
                int lastPageTop = contentBottom - displayHeight;

                // The scrolled top position of scroll view, in pixels.
                int currScrollY = mScrollView.getScrollY();
                // The scrolled middle position of scroll view, in pixels.
                int currScrollMiddleY = currScrollY + displayHeight / 2 - contentTop;
                int currPageMiddleY = ((currentPage+1) * displayHeight) - displayHeight/2;


                // Current page num.
                int exactCurrPage = currScrollMiddleY / displayHeight;

                // Next page num.
                int currScrollPageRelative = currScrollMiddleY - displayHeight * currentPage;
                int nextPage = currentPage;



                boolean fastScroll = false;
                scrollDirection = (event.getY() - first_y) / (event.getEventTime() - first_time);




                //FAST SCROLL MODE 2: SCROLLA TUTTO IL FRAGMENT
                if( Dy < -treshold_Dy_scrolling ) {
                    // scorro in basso

                    if(fastScrollJumpFragment)
                    {
                        if (activeFragment < fragmentFirstPageMap.length - 1)
                        {
                            // se non è l'ultimo fragment
                            activeFragment++;
                            nextPage = fragmentFirstPageMap[activeFragment];
                        }
                        else
                        {
                            // altrimenti se è l'ultimo fragment, va all'ultima pagina dell'ultimo fragment
                            nextPage = fragmentFirstPageMap[activeFragment] + fragmentNPageMap[activeFragment] - 1;
                        }
                        fastScroll = true;
                    }
                    else
                    {
                        if(nextPage < pageFragmentMap.length - 1)
                        {
                            nextPage++;
                            activeFragment = pageFragmentMap[nextPage];
                        }
                    }
                }
                else if( Dy > treshold_Dy_scrolling ) {
                    // scorro in alto

                    if(activeFragment > 0)
                    {
                        // se non è il primo fragment
                        activeFragment--;
                        nextPage = fragmentFirstPageMap[activeFragment] + fragmentNPageMap[activeFragment]-1;
                    }
                    else
                    {
                        // altrimenti se è il pimo fragment, va alla prima pagina del primo fragment
                        nextPage = fragmentFirstPageMap[activeFragment]; // = 0;
                    }
                    fastScroll = true;
                }




                double currentPageMiddleY = ((currentPage+1) * displayHeight) - (displayHeight/2);


                if(currScrollMiddleY < currentPageMiddleY)
                {
                    if(!fastScroll && currScrollPageRelative < displayHeight * treshold_y_scrolling_percent)
                        nextPage = currentPage - 1;

                    if(nextPage <= 0) nextPage = 0;

                    double zonaInvasioneMiddle = displayHeight * ( fragmentFirstPageMap[activeFragment] + 1 ) - displayHeight/2;

                    if( stdScrollIfHigher || currScrollMiddleY < zonaInvasioneMiddle || pageFragmentMap[nextPage] != activeFragment|| fastScroll )
                    {
                        // The top of next page, in pixels.
                        int nextPageTop = contentTop + nextPage * displayHeight;
                        int min = Math.min(lastPageTop, nextPageTop);
                        int max = Math.max(min, contentTop);
                        scroller.startScroll(0, currScrollY, 0, max - currScrollY, 500);// Start scrolling calculation.
                        mScrollView.post(pageScrollTask);// Start animation.
                    }
                }
                else if(currScrollMiddleY > currentPageMiddleY)
                {
                    if (!fastScroll && currScrollPageRelative > displayHeight * (1-treshold_y_scrolling_percent) )
                        nextPage= currentPage +1;

                    if(nextPage >= this.nPages) nextPage = this.nPages-1;

                    double zonaInvasioneMiddle = displayHeight * (fragmentNPageMap[activeFragment] + fragmentFirstPageMap[activeFragment] ) - displayHeight/2;


                    if(stdScrollIfHigher || currScrollMiddleY > zonaInvasioneMiddle || pageFragmentMap[nextPage] != activeFragment|| fastScroll )
                    {
                        // The top of next page, in pixels.
                        int nextPageTop = contentTop + nextPage * displayHeight;

                        int min = Math.min(lastPageTop, nextPageTop);
                        int max = Math.max(min, contentTop);
                        scroller.startScroll(0, currScrollY, 0, max - currScrollY, 500);// Start scrolling calculation.
                        mScrollView.post(pageScrollTask);// Start animation.
                    }


                }

                currentPage = nextPage;
                activeFragment = pageFragmentMap[currentPage];




                if(beforeTouchPage != currentPage)
                {
                    if(pageProgressBar != null)
                        pageProgressBar.setActiveDot(currentPage);
                    if(scrollListener != null)
                        scrollListener.onPageChanged(currentPage, beforeTouchPage, pageFragmentMap[currentPage], pageFragmentMap[beforeTouchPage] );
                    // EVENTO: abbiamo cambiato pagina
                    if(beforeTouchFragment != pageFragmentMap[currentPage])
                    {
                        if(scrollListener != null)
                            scrollListener.onFragmentChanged(beforeTouchFragment, pageFragmentMap[currentPage]);
                        // EVENTO: abbiam ocambiato fragment

                        if(fragmentProgressBar != null)
                            fragmentProgressBar.setActiveDot(pageFragmentMap[currentPage]);
                    }
                }




                return true;
            }


        }



		
		return false;
	}




    public final int nFragment() {
        return this.nFragments;
    }


    public final int getActiveFragment() {
        return this.pageFragmentMap[currentPage];
    }

    public final int getActivePage() {
        return currentPage;
    }
}
