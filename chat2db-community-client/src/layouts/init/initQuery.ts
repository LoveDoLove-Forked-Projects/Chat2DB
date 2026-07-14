import { GuideDialogStatus } from '@/components/GuideDialog/type';
import { runtimeEditionConfig } from '@/constants/runtimeEdition';
import { useGlobalStore } from '@/store/global';
import { useOrgStore } from '@/store/organization';
import { useUserStore } from '@/store/user';
import { removeOpenScreenAnimation } from '@/utils/dom';
import { trackSignUp } from '@/utils/googleAds';
import { isEmbedIframePage } from '@/utils/iframe';
import dayjs from 'dayjs';
import { useEffect, useLayoutEffect, useState } from 'react';
import useGlobalData from './useGlobalData';

const useInitQuery = () => {
  // Whether all initQuery requests have been completed
  const [initQueryLoaded, setInitQueryLoaded] = useState(false);
  const getGlobalData = useGlobalData();
  const { queryCurUser, setPricingModalStatus } = useUserStore((state) => {
    return {
      queryCurUser: state.queryCurUser,
      setPricingModalStatus: state.setPricingModalStatus,
    };
  });

  const { curOrg, curOrgSubscription, queryOrgList } = useOrgStore((state) => ({
    curOrg: state.curOrg,
    curOrgSubscription: state.curOrgSubscription,
    queryOrgList: state.queryOrgList,
  }));

  const { appConfig, isEmbedIframe, setOpenGuideDialog, setGuideDialogStatus, setConfetti, fetchSpm } = useGlobalStore(
    (state) => ({
      appConfig: state.appConfig,
      isEmbedIframe: state.isEmbedIframe,
      setOpenGuideDialog: state.setOpenGuideDialog,
      setGuideDialogStatus: state.setGuideDialogStatus,
      setConfetti: state.setConfetti,
      fetchSpm: state.fetchSpm,
    }),
  );

  const { isReady } = appConfig;

  useLayoutEffect(() => {
    if (isReady) {
      removeOpenScreenAnimation();
      // Get user information
      queryCurUser()
        .then(() => {
          if (runtimeEditionConfig.spmTracking) {
            fetchSpm();
          }
          queryOrgList();
          getGlobalData();
        })
        .finally(() => {
          setInitQueryLoaded(true);
        });
    }
  }, [appConfig.curCountry, isReady]);

  const isNewAccount = curOrg && dayjs().diff(dayjs(curOrg?.createTime), 'seconds') < 5;
  const hasExpiredSubscription = curOrgSubscription && dayjs().diff(dayjs(curOrgSubscription.endTime)) > 0;

  // Registration conversion trigger: no longer relies on curOrg.createTime - createTime returned by the backend has time zone deviation
  // (up to ±16 hours in overseas server scenarios), any window of "number creation within N seconds/minutes" is unreliable.
  // Instead, use "authentication just completed on the login page" as the signal: the login page writes the sessionStorage tag in the authentication action.
  // (edition independent sessionStorage tag, still survives jumps across third-party logins), enter the app and report it once after getting the organization.
  // And googleAds internally removes duplication according to the localStorage of orgId, ensuring that each organization can report it at most once.
  useEffect(() => {
    if (!runtimeEditionConfig.googleAds) {
      return;
    }
    if (!curOrg) {
      return;
    }
    let pending = false;
    try {
      pending = !!sessionStorage.getItem(runtimeEditionConfig.googleAdsSignupPendingStorageKey);
    } catch {
      pending = false;
    }
    if (!pending) {
      return;
    }
    try {
      sessionStorage.removeItem(runtimeEditionConfig.googleAdsSignupPendingStorageKey);
    } catch {
      // Ignored when sessionStorage is unavailable
    }
    trackSignUp({ orgId: curOrg.id });
  }, [curOrg]);

  useEffect(() => {
    // There are only 5 seconds left to create an account, and it is not a VIP. Display the pop-up box when entering for the first time
    if (!curOrg || curOrg?.vip || isEmbedIframe || isEmbedIframePage() || !isNewAccount) {
      return;
    }

    setOpenGuideDialog(true);
    setGuideDialogStatus(GuideDialogStatus.FirstLogin);

    let hideConfettiTimer: ReturnType<typeof setTimeout> | undefined;
    const showConfettiTimer = setTimeout(() => {
      setConfetti(true);
      hideConfettiTimer = setTimeout(() => {
        setConfetti(false);
      });
    }, 100);

    return () => {
      clearTimeout(showConfettiTimer);
      if (hideConfettiTimer) {
        clearTimeout(hideConfettiTimer);
      }
    };
  }, [
    curOrg?.createTime,
    curOrg?.id,
    curOrg?.vip,
    isEmbedIframe,
    isNewAccount,
    setConfetti,
    setGuideDialogStatus,
    setOpenGuideDialog,
  ]);

  useEffect(() => {
    // Member expiration pop-up display
    if (
      !curOrg ||
      curOrg?.vip ||
      isEmbedIframe ||
      isEmbedIframePage() ||
      !curOrgSubscription ||
      !hasExpiredSubscription ||
      !shouldShowPopup('vip-expire')
    ) {
      return;
    }

    setOpenGuideDialog(true);
    setGuideDialogStatus(GuideDialogStatus.Expired);
    setConfetti(false);
  }, [
    curOrg?.id,
    curOrg?.vip,
    curOrgSubscription?.endTime,
    hasExpiredSubscription,
    isEmbedIframe,
    setConfetti,
    setGuideDialogStatus,
    setOpenGuideDialog,
  ]);

  useEffect(() => {
    // The price pop-up box will automatically pop up for non-members (excluding the first login and membership expiration scenarios, it will no longer pop up automatically within one day after closing)
    if (
      !curOrg ||
      curOrg?.vip ||
      !runtimeEditionConfig.pricingAutoPopup ||
      isEmbedIframe ||
      isEmbedIframePage() ||
      isNewAccount ||
      hasExpiredSubscription ||
      !shouldShowPricingAutoPopup()
    ) {
      return;
    }

    setPricingModalStatus(true);
  }, [curOrg?.id, curOrg?.vip, hasExpiredSubscription, isEmbedIframe, isNewAccount, setPricingModalStatus]);

  return {
    initQueryLoaded,
  };
};

export default useInitQuery;

function shouldShowPopup(str: string) {
  const storageKey = runtimeEditionConfig.dailyPopupStorageKeyPrefix
    ? `${runtimeEditionConfig.dailyPopupStorageKeyPrefix}:${str}`
    : str;
  // Get today's date (excluding time)
  const today = new Date().toDateString();

  // Get the date of the last pop-up display from localStorage
  const lastPopupDate = localStorage.getItem(storageKey);

  // Check whether the popup should be displayed
  if (lastPopupDate !== today) {
    // Update the date displayed in the pop-up box
    localStorage.setItem(storageKey, today);
    // Returns true, indicating that the pop-up box should be displayed
    return true;
  }

  // Return false, indicating that the pop-up box should not be displayed
  return false;
}

const PRICING_AUTO_POPUP_KEY = runtimeEditionConfig.pricingAutoPopupStorageKey;

function shouldShowPricingAutoPopup(): boolean {
  const dismissedAt = localStorage.getItem(PRICING_AUTO_POPUP_KEY);
  if (dismissedAt) {
    const elapsed = Date.now() - Number(dismissedAt);
    // It will no longer pop up automatically within one day after closing.
    if (elapsed < 24 * 60 * 60 * 1000) {
      return false;
    }
  }
  // Mark as popped to prevent repeated triggering in the same rendering cycle
  localStorage.setItem(PRICING_AUTO_POPUP_KEY, String(Date.now()));
  return true;
}

export { PRICING_AUTO_POPUP_KEY };
