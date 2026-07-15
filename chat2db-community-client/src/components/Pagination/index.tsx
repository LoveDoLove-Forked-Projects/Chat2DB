import { useEffect, useState } from 'react';
import { InputNumber, Tooltip, Dropdown } from 'antd';
import { IResultConfig } from '@/typings';
import i18n from '@/i18n';
import _ from 'lodash';
import { IconButton, ToolbarBtn } from '@chat2db/ui';
import LoadingGracile from '@/components/Loading/LoadingGracile';
import { useStyles } from './style';
import { DownOutlined } from '@ant-design/icons';

interface IProps {
  onPageSizeChange?: (pageSize: number) => void;
  onPageNoChange?: (pageNo: number) => void;
  onClickTotalBtn?: () => Promise<number | undefined>;
  paginationConfig: IResultConfig;
}

type IIconType = 'pre' | 'next' | 'first' | 'last';

export default function Pagination(props: IProps) {
  const { onPageNoChange, onPageSizeChange, paginationConfig } = props;
  const [inputNumberWidth, setInputNumberWidth] = useState<number>(25);
  const { styles } = useStyles({ inputNumberWidth });
  const [inputValue, setInputValue] = useState<number | null>(1);
  const [totalLoading, setTotalLoading] = useState(false);

  useEffect(() => {
    setInputValue(paginationConfig?.pageNo ?? 1);
  }, [paginationConfig?.pageNo]);

  const onInputNumberChange = (value: number | null) => {
    setInputValue(value);
  };

  useEffect(() => {
    const width = (inputValue?.toString().length ?? 1) * 8 + 17;
    // if (width > 105) {
    //   width = 105;
    // }
    setInputNumberWidth(width);
  }, [inputValue]);

  const onInputNumberBlur = () => {
    if (_.isNumber(inputValue)) {
      if (inputValue !== paginationConfig?.pageNo) {
        onPageNoChange && onPageNoChange(inputValue);
      }
    } else {
      setInputValue(1);
      onPageNoChange && onPageNoChange(1);
    }
  };

  const handleClickTotalBtn = async () => {
    if (!props.onClickTotalBtn) return;
    setTotalLoading(true);

    try {
      const res = await props.onClickTotalBtn();
      return res;
    } catch (error) {
      console.error(error);
    } finally {
      setTotalLoading(false);
    }
  };

  const handleClickIcon = async (type: IIconType) => {
    if (!onPageNoChange || !paginationConfig) return;
    if (handleIsDisabled(type)) return;
    switch (type) {
      case 'first':
        onPageNoChange(1);
        break;
      case 'last':
        {
          const total = await handleClickTotalBtn();
          const { pageSize } = paginationConfig || {};
          if (_.isNumber(total) && _.isNumber(pageSize)) {
            props.onPageNoChange && props.onPageNoChange(Math.ceil(total / pageSize));
          }
        }
        break;
      case 'pre':
        onPageNoChange(paginationConfig?.pageNo - 1);
        break;
      case 'next':
        onPageNoChange(paginationConfig?.pageNo + 1);
        break;
      default:
        break;
    }
  };

  const handleIsDisabled = (type: IIconType) => {
    if (!paginationConfig) {
      return false;
    }
    if (type === 'first') {
      return paginationConfig?.pageNo === 1;
    }
    if (type === 'pre') {
      return paginationConfig?.pageNo === 1;
    }

    const isNumber = _.isNumber(paginationConfig.total);
    const totalShow = paginationConfig.pageNo * paginationConfig.pageSize;
    if (type === 'next' || type === 'last') {
      if (isNumber) {
        return totalShow > (paginationConfig.total as number);
      }
      return !paginationConfig?.hasNextPage;
    }

    return true;
  };

  const items: any = [
    {
      label: '10',
      value: 10,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(10);
      },
    },
    {
      label: '100',
      value: 100,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(100);
      },
    },
    {
      label: '200',
      value: 200,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(200);
      },
    },
    {
      label: '500',
      value: 500,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(500);
      },
    },
    {
      label: '1000',
      value: 1000,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(1000);
      },
    },
    {
      label: '5000',
      value: 5000,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(5000);
      },
    },
    {
      label: '10000',
      value: 10000,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(10000);
      },
    },
    {
      label: '50000',
      value: 50000,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(50000);
      },
    },
    {
      label: '100000',
      value: 100000,
      onClick: () => {
        onPageSizeChange && onPageSizeChange(100000);
      },
    },
  ];

  return (
    <div className={styles.paginationWrapper}>
      <IconButton
        code="icon-paging-start"
        disabled={handleIsDisabled('first')}
        size="sm"
        onClick={() => handleClickIcon('first')}
      />
      <IconButton
        code="icon-paging-left"
        disabled={handleIsDisabled('pre')}
        size="sm"
        onClick={() => handleClickIcon('pre')}
      />
      <InputNumber
        className={styles.inputNumber}
        size="small"
        min={1}
        value={inputValue}
        controls={false}
        onPressEnter={onInputNumberBlur}
        onBlur={onInputNumberBlur}
        onChange={onInputNumberChange}
      />
      <IconButton
        code="icon-paging-right"
        disabled={handleIsDisabled('next')}
        size="sm"
        onClick={() => handleClickIcon('next')}
      />
      <IconButton
        code="icon-paging-end"
        disabled={handleIsDisabled('last')}
        size="sm"
        onClick={() => handleClickIcon('last')}
      />

      <Dropdown destroyPopupOnHide menu={{ items }}>
        <div className={styles.selectSize}>
          {paginationConfig?.pageSize ?? 200}
          <DownOutlined />
        </div>
      </Dropdown>
      {props.onClickTotalBtn ? (
        <Tooltip mouseEnterDelay={0.6} title={i18n('workspace.table.total.tip')}>
          <ToolbarBtn
            text={`${i18n('workspace.table.total')}：${paginationConfig?.total}`}
            className={styles.totalButton}
            suffixIcon={totalLoading ? <LoadingGracile /> : ''}
            onClick={handleClickTotalBtn}
          />
        </Tooltip>
      ) : (
        <div className={styles.totalContainer}>
          {i18n('workspace.table.total')} ：{paginationConfig?.total}
        </div>
      )}
    </div>
  );
}
