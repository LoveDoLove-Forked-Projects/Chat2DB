import { useUpdateEffect } from 'ahooks';
import React, { useEffect, useRef } from 'react';

function getScrollParent(ref) {
  let parent = ref.current.parentNode;

  while (parent) {
    if (parent === document.body || parent === document.documentElement) {
      return document.body;
    }

    const style = getComputedStyle(parent);
    if (style.overflow !== 'visible' || style.overflowX !== 'visible' || style.overflowY !== 'visible') {
      return parent;
    }

    parent = parent.parentNode;
  }

  return null;
}

const useScrollToBottom = (
  bottomRef: React.RefObject<HTMLElement>,
  triggerVariable: any,
  behavior?: ScrollBehavior,
) => {
  const isScrolledToBottomRef = useRef(true);
  const isUserScroll = useRef(false); // Add new logo
  const scrollDomRef = useRef(null);

  useEffect(() => {
    if (!bottomRef.current) return;
    const scrollDom = getScrollParent(bottomRef);
    scrollDomRef.current = scrollDom;
    if (scrollDom) {
      const checkScroll = () => {
        // if (isUserScroll.current) {
        //   // Check if scrolling is triggered by user
        //   const isScrolledToBottom = scrollDom.scrollTop === scrollDom.scrollHeight - scrollDom.clientHeight;
        //   isScrolledToBottomRef.current = isScrolledToBottom;
        //   console.log('isScrolledToBottom', isScrolledToBottom, 'isUserScroll', isUserScroll.current);
        //   isUserScroll.current = false; // Reset flag
        // }
      };

      const onScroll = () => {
        isUserScroll.current = true; // set flag
        checkScroll();
      };

      scrollDom.addEventListener('scroll', onScroll);
      checkScroll();

      return () => {
        scrollDom.removeEventListener('scroll', onScroll);
      };
    }
  }, [bottomRef.current]);

  useUpdateEffect(() => {
    setTimeout(() => {
      scrollToBottom();
    }, 0);
  }, [triggerVariable]);

  const scrollToBottom = () => {
    bottomRef.current?.scrollIntoView({
      behavior: behavior || 'auto',
    });
  }

  return { scrollToBottom };
};

export default useScrollToBottom;
