import { useCallback, useMemo, useState, useEffect, useRef } from 'react';
import classnames from 'classnames';
import { Form, Input, Modal, type InputRef } from 'antd';
import MonacoEditor, { IExportRefFunction } from '@/components/MonacoEditor';
import { v4 as uuid } from 'uuid';
import sqlService from '@/service/sql';
import i18n from '@/i18n';
import { debounce } from 'lodash';
import { DatabaseTypeCode } from '@/constants';
import { useWorkspaceStore } from '@/store/workspace';
import { useStyles } from './style';

// TODO: This can warn that the `useForm` instance is not connected to a Form element.
// The !!relyOnParams && condition can prevent the form from being created.
// Needs resolution.

interface IProps {
  relyOnParams: {
    databaseType: DatabaseTypeCode;
    dataSourceId: number;
    databaseName?: string;
  };
  executedCallback?: () => void;
}

export type CreateType = 'database' | 'schema';

export interface ICreateDatabase {
  databaseName?: string;
  schemaName?: string;
  comment?: string;
}

// Databases that do not support comments during creation.
const noCommentDatabase = [DatabaseTypeCode.MYSQL];

const CreateDatabase = () => {
  const { styles } = useStyles();
  const [form] = Form.useForm<ICreateDatabase>();
  const monacoEditorUuid = useMemo(() => uuid(), []);
  const monacoEditorRef = useRef<IExportRefFunction>(null);
  const [open, setOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState<{ success: boolean; message: string; originalSql: string } | null>(
    null,
  );
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [createType, setCreateType] = useState<CreateType>('database');
  const [relyOnParams, setRelyOnParams] = useState<IProps['relyOnParams'] | null>(null);
  const executedCallbackRef = useRef<IProps['executedCallback']>();
  const setOpenCreateDatabaseModal = useWorkspaceStore((state) => state.setOpenCreateDatabaseModal);
  const inputRef = useRef<InputRef>(null);

  useEffect(() => {
    if (!open) {
      setErrorMessage(null);
      form.resetFields();
      monacoEditorRef.current?.setValue('', 'cover');
    } else {
      setTimeout(() => {
        inputRef.current?.focus();
      }, 0);
    }
  }, [open]);

  const config = useMemo(() => {
    return createType === 'database'
      ? {
          title: `${i18n('common.title.create')} Database`,
          api: sqlService.getCreateDatabaseSql,
          formName: 'databaseName',
        }
      : {
          title: `${i18n('common.title.create')} Schema`,
          api: sqlService.getCreateSchemaSql,
          formName: 'schemaName',
        };
  }, [createType]);

  const labelCol = { flex: '70px' };

  const handleFieldsChange = useCallback(
    debounce(() => {
      const formData: ICreateDatabase = form.getFieldsValue();
      if (!formData.databaseName && createType === 'database') {
        return;
      }
      if (!formData.schemaName && createType === 'schema') {
        return;
      }
      const params = {
        databaseType: relyOnParams?.databaseType,
        dataSourceId: relyOnParams?.dataSourceId,
        databaseName: relyOnParams?.databaseName,
        ...formData,
      };
      config.api(params as any).then((res) => {
        const { sql } = res;
        monacoEditorRef.current?.setValue(sql, 'cover');
      });
    }, 500),
    [relyOnParams, createType, monacoEditorRef, config],
  );

  const executeUpdateDataSql = (sql: string) => {
    const params: any = {
      dataSourceId: relyOnParams?.dataSourceId,
      databaseType: relyOnParams?.databaseType,
      databaseName: relyOnParams?.databaseName,
      sql,
    };
    setConfirmLoading(true);
    setErrorMessage(null);
    return sqlService
      .executeDDL(params)
      .then((res) => {
        if (res.success) {
          setOpen(false);
          executedCallbackRef.current?.();
        } else {
          setErrorMessage(res);
        }
      })
      .finally(() => {
        setConfirmLoading(false);
      });
  };

  const onOk = () => {
    const sql = monacoEditorRef.current?.getAllContent() || '';
    executeUpdateDataSql(sql);
  };

  const openCreateDatabaseModal = (params: {
    type: CreateType;
    relyOnParams: {
      databaseType: DatabaseTypeCode;
      dataSourceId: number;
      databaseName?: string;
    };
    executedCallback?: () => void;
  }) => {
    setOpen(true);
    setCreateType(params.type);
    setRelyOnParams(params.relyOnParams);
    executedCallbackRef.current = params.executedCallback;
  };

  useEffect(() => {
    setOpenCreateDatabaseModal(openCreateDatabaseModal);
  }, []);

  return (
    !!relyOnParams && (
      <Modal
        onCancel={() => {
          setOpen(false);
        }}
        maskClosable={false}
        title={config.title}
        destroyOnClose
        confirmLoading={confirmLoading}
        open={open}
        onOk={onOk}
      >
        <div className={styles.createDatabaseDom}>
          <Form labelAlign="left" form={form} labelCol={labelCol} onFieldsChange={handleFieldsChange} name="create">
            <Form.Item label={i18n('common.label.name')} name={config.formName}>
              <Input ref={inputRef} autoComplete="off" />
            </Form.Item>
            {noCommentDatabase.includes(relyOnParams.databaseType) ? null : (
              <Form.Item label={i18n('common.label.comment')} name="comment">
                <Input autoComplete="off" />
              </Form.Item>
            )}
          </Form>
          <div className={styles.previewBox}>
            <div className={styles.previewText}>{i18n('common.title.preview')}</div>
            <div className={styles.previewLine} />
          </div>
          <div className={styles.monacoEditorBox}>
            <MonacoEditor
              ref={monacoEditorRef}
              options={{
                lineNumbers: 'off',
              }}
              autoFocus={false}
              id={monacoEditorUuid}
            />
          </div>
          {errorMessage && (
            <>
              <div className={classnames(styles.previewBox, styles.errorBox)}>
                <div className={styles.previewText}>{i18n('common.title.errorMessage')}</div>
                <div className={styles.previewLine} />
              </div>
              <div>{errorMessage.message}</div>
            </>
          )}
        </div>
      </Modal>
    )
  );
};

export default CreateDatabase;
