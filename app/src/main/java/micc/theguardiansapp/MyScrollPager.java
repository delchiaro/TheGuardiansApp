
package micc.theguardiansapp;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

public class MyScrollPager implements OnTouchListener
{
	// The class encapsulates scrolling.(Overshoot)
	private Scroller scroller;
	// The task make scroll view scrolled.
	private Runnable pageScrollTask;
    private Runnable fragmentScrollTask;


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

	public MyScrollPager(ScrollView aScrollView, ViewGroup aContentView, ViewGroup[] aFragmentContainer)
	{
		mScrollView = aScrollView;
        mContentView = aContentView;
        mFragmentContainers = aFragmentContainer;
        nFragments = mFragmentContainers.length;
        activeFragment = 0;


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

        this.displayHeight = mScrollView.getHeight();

        int nPageTotal = 0;
        int i = 0;
        for( ViewGroup v : mFragmentContainers)
        {

            // set the heght for each fragment container as a multiple of displayHeight
            double vHeight = v.getHeight();
            double rapporto = vHeight / displayHeight;
            double ceil = Math.ceil(rapporto);

            int nPage = (int)ceil;
            nPageTotal += nPage;
            //int nDisplay = (int) Math.ceil(v.getHeight() / displayHeight);
            v.setMinimumHeight(nPage*displayHeight);

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

        int a = 2;

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

//
//               // FAST SCROLL MODE 1: SCROLLA SOLO SE IL FRAGMENT HA UNA SOLA PAGINA
//                if(Dy > treshold_Dy_scrolling )
//                {
//                    if(fragmentNPageMap[activeFragment] == 1)
//                    {
//                        // PER ADESSO DISABILITO FAST SCROLL SU FRAGMENT CON PIÙ PAGINE
//                        fastScroll = true;
//                        nextPage--;
//                    }
//                }
//                else if( Dy < -treshold_Dy_scrolling)
//                {
//
//                    if(fragmentNPageMap[activeFragment] == 1)
//                    {
//                        // PER ADESSO DISABILITO FAST SCROLL SU FRAGMENT CON PIÙ PAGINE
//                        fastScroll = true;
//                        nextPage++;
//                    }
//
//                }



                //FAST SCROLL MODE 2: SCROLLA TUTTO IL FRAGMENT
                // NON FUNZIONA ANCORA BENE.. FIXARE!!(certe volte scrolla la pagina nello stesso fragment)
                if( Dy < -treshold_Dy_scrolling ) {
                    // scorro in basso

                    if(activeFragment < fragmentFirstPageMap.length - 1)
                    {
                        // se non è l'ultimo fragment
                        activeFragment++;
                        nextPage = fragmentFirstPageMap[activeFragment];
                    }
                    else
                    {
                        // altrimenti se è l'ultimo fragment, va all'ultima pagina dell'ultimo fragment
                        nextPage = fragmentFirstPageMap[activeFragment] + fragmentNPageMap[activeFragment]-1;
                    }
                    fastScroll = true;

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
                        nextPage = currentPage -1;

                    if(nextPage <= 0) nextPage = 0;

                    double zonaInvasioneMiddle = displayHeight * ( fragmentFirstPageMap[activeFragment] + 1 ) - displayHeight/2;

                    if( currScrollMiddleY < zonaInvasioneMiddle || pageFragmentMap[nextPage] != activeFragment|| fastScroll )
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


                    if(currScrollMiddleY > zonaInvasioneMiddle || pageFragmentMap[nextPage] != activeFragment|| fastScroll )
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

                return true;
            }
        }
		
		return false;
	}
}
