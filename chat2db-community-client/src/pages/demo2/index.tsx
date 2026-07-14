import React, { useEffect, useState } from 'react';
import { useStyles } from './style';
import { Button, Flex } from 'antd';
// import exampleSQL from '@/components/SQLEditor/data/example.sql';
import exampleSQL1 from '@/components/SQLEditor/data/editor1.sql';
import exampleSQL2 from '@/components/SQLEditor/data/editor2.sql';
// import MonacoSQLEditor from '@/components/SQLEditor/MonacoEditor';
import { MonacoEditor, SORT_TEXT, SQLEditor, SQLEditorWithOperation, TIP_TYPE } from '@/components/SQLEditor';
import { DatabaseTypeCode, WorkspaceTabType } from '@/constants';
import EditorTest from './moncao-editor';
import SQLEditorDemo from './sql-editor';
import * as monaco from 'monaco-editor';
import { ITipItemVO } from '@/typings/sqlParser';
import keywordObj from '@/components/SQLEditor/helper/keywords';
import i18n from '@/i18n';

const Demo = () => {
  const { styles } = useStyles();

  const [activeIndex, setActiveIndex] = useState(0);
  // Instance-level hover provider.
  // const instanceHoverProvider = async (word: string) => {
  //   // Send an API request here to retrieve the data.
  //   // Example: const data = await fetch(`/api/sql-hint?word=${word}`).then(res => res.json());
  //   // Return null when no relevant data is found.
  //   // This is only an example.
  //   if (word.toUpperCase() === 'USERS') {
  //     return {
  //       explanation: 'Table containing user information',
  //       detail: 'Specific to this database schema',
  //     };
  //   }
  //   return null;
  // };

  // const [value, setValue] = useState('');

  // const [sql1, setSql1] = useState(exampleSQL1);
  // const [sql2, setSql2] = useState(exampleSQL2);

  // const handleSql1Change = (value: string) => {
  //   console.log('sql1===>', value);
  //   setSql1(value);
  // };

  // const handleSql2Change = (value: string) => {
  //   console.log('sql2===>', value);
  //   setSql2(value);
  // };

  // const [dbInfo, setDbInfo] = useState({
  //   dataSourceId: 373,
  //   databaseName: 'zgq_test',
  //   schemaName: '',
  //   databaseType: DatabaseTypeCode.MYSQL,
  // });

  // const handleDbInfoChange = (field: keyof typeof dbInfo) => (event: React.ChangeEvent<HTMLInputElement>) => {
  //   setDbInfo((prev) => ({
  //     ...prev,
  //     [field]: field === 'dataSourceId' ? Number(event.target.value) : event.target.value,
  //   }));
  // };

  // return <EditorTest />;
  // return <SQLEditorDemo />;

  const [ddl, setDdl] = useState('');
  useEffect(() => {
    // initProvider();
    // registerBuiltInKeywordsProvider();
  }, []);

  const initProvider = () => {
    console.log('initProvider');
    monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems: () => {
        console.log('provideCompletionItems');
        return {
          suggestions: [
            {
              label: 'demo',
              kind: monaco.languages.CompletionItemKind.Keyword,
              insertText: 'demo',
            },
            {
              label: 'demo1',
              kind: monaco.languages.CompletionItemKind.Keyword,
              insertText: 'demo1',
            },
            {
              label: 'demo2',
              kind: monaco.languages.CompletionItemKind.Keyword,
              insertText: 'demo2',
            },
          ],
        };
      },
    });
  };

  const registerBuiltInKeywordsProvider = () => {
    const { keywords, priority_keywords } = keywordObj[DatabaseTypeCode.MYSQL];
    const keywordTips: ITipItemVO[] = (keywords || []).map((key: string) => ({
      value: key.toLowerCase(),
      type: TIP_TYPE.KEYWORD,
      dataType: '',
      comment: '',
      datasourceName: '',
      databaseName: '',
      schemaName: '',
      tableName: '',
    }));

    const priorityKeywordTips: ITipItemVO[] = (priority_keywords || []).map((key: string) => ({
      value: key.toLowerCase(),
      type: TIP_TYPE.KEYWORD,
      dataType: '',
      comment: '',
      datasourceName: '',
      databaseName: '',
      schemaName: '',
      tableName: '',
      sortText: `${SORT_TEXT.KEYWORD_HIGH_PRIORITY}_${key}`.toLowerCase(),
    }));

    const keywordCase = true; // useGlobalStore.getState().editorSettings?.keywordCase;

    const tips = priorityKeywordTips
      .map((k) => ({
        ...k,
        value: keywordCase ? k.value.toUpperCase() : k.value.toLowerCase(),
      }))
      .concat(
        [...keywordTips].map((cur: ITipItemVO, index) => ({
          ...cur,
          value: keywordCase ? cur.value.toUpperCase() : cur.value.toLowerCase(),
          sortText: handleSortText(cur, index),
        })),
      );

    const suggestions = handleKeywordCompletionItems(tips);

    monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems: (model, position) => {
        console.log('builtInKeywordsProvider', model, position);
        return {
          suggestions,
          incomplete: false,
        };
      },
    });
  };

  const handleKeywordCompletionItems = (keywords: ITipItemVO[]) => {
    return (keywords || []).map((keyword) => constructSuggestion(keyword));
  };
  const constructSuggestion = (item: ITipItemVO): monaco.languages.CompletionItem => {
    const { type, insertTextRules = monaco.languages.CompletionItemInsertTextRule.None, sortText } = item;
    // console.log(item.value, sortText, type);
    const suggestion: monaco.languages.CompletionItem = {
      label: {
        label: item.value,
        detail: item.detail || getTipsDetail(item, type),
        description: item.description || getTipDescription(item, type),
      },
      documentation: {
        value: item.comment,
        isTrusted: true,
      },
      kind: getCompletionItemKind(type),
      insertText: item.insertText || handleAllTypeSpecialName(item.value, item.type),
      insertTextRules,
      sortText,
    };
    return suggestion;
  };

  const handleSortText = (tip: ITipItemVO, index?: number): string => {
    const { value } = tip;

    const typeWord = SORT_TEXT[tip.type];

    // Pad the value to keep sorting stable.
    // console.log('handleSortText', typeWord, value);
    return `${typeWord}${index ?? ''}${(value || '').padEnd(8, 'a')}`.toLowerCase();
  };

  const getTipsDetail = (item: ITipItemVO, type: TIP_TYPE): string => {
    let detail = '';
    switch (type) {
      case TIP_TYPE.DATABASE:
        detail = `(${item.datasourceName})`;
        break;
      case TIP_TYPE.SCHEMA:
        detail = `(${item.datasourceName})`;
        break;
      case TIP_TYPE.TABLE:
        detail = `(${item.databaseName || item.schemaName})`;
        break;
      case TIP_TYPE.VIEW:
        detail = `(${item.databaseName || item.schemaName})`;
        break;
      case TIP_TYPE.COLUMN:
        detail = `(${item.tableName})`;
        break;
      case TIP_TYPE.JOIN_CLAUSE:
        detail = `(${item.tableName})`;
        break;
      default:
        break;
    }

    return detail;
  };

  const getTipDescription = (item: ITipItemVO, type: TIP_TYPE): string => {
    let description = '';
    switch (type) {
      case TIP_TYPE.KEYWORD:
        description = i18n('monaco.completion.keyword');
        break;
      case TIP_TYPE.DATABASE:
        description = i18n('monaco.completion.database');
        break;
      case TIP_TYPE.SCHEMA:
        description = i18n('monaco.completion.schema');
        break;
      case TIP_TYPE.TABLE:
        description = i18n('monaco.completion.table');
        break;
      case TIP_TYPE.COLUMN:
        description = item?.dataType ?? i18n('monaco.completion.column');
        break;
      case TIP_TYPE.VIEW:
        description = i18n('monaco.completion.view');
        break;
      case TIP_TYPE.FUNCTION:
        description = i18n('monaco.completion.function');
        break;
      case TIP_TYPE.PARAMETER:
        // description = i18n('monaco.completion.parameter');
        break;
      case TIP_TYPE.PROCEDURE:
        description = i18n('monaco.completion.procedure');
        break;
      case TIP_TYPE.TRIGGER:
        description = i18n('monaco.completion.trigger');
        break;
      case TIP_TYPE.JOIN_CLAUSE:
        description = i18n('monaco.completion.joinClause');
        break;
      default:
        description = '';
        break;
    }
    return description;
  };

  const handleAllTypeSpecialName = (name: string, type: TIP_TYPE): string => {
    if (
      [
        TIP_TYPE.DATABASE,
        TIP_TYPE.SCHEMA,
        TIP_TYPE.TABLE,
        TIP_TYPE.FUNCTION,
        TIP_TYPE.VIEW,
        TIP_TYPE.PROCEDURE,
      ].includes(type)
    ) {
      return name;
    }
    return name;
  };

  const getCompletionItemKind = (type: TIP_TYPE): monaco.languages.CompletionItemKind => {
    switch (type) {
      case TIP_TYPE.SNIPPET:
        return monaco.languages.CompletionItemKind.Snippet;
      case TIP_TYPE.DATABASE:
        return monaco.languages.CompletionItemKind.Method;
      case TIP_TYPE.SCHEMA:
        return monaco.languages.CompletionItemKind.Module;
      case TIP_TYPE.TABLE:
        return monaco.languages.CompletionItemKind.Class;
      case TIP_TYPE.COLUMN:
        return monaco.languages.CompletionItemKind.Field;
      case TIP_TYPE.VIEW:
        return monaco.languages.CompletionItemKind.Interface;
      case TIP_TYPE.FUNCTION:
        return monaco.languages.CompletionItemKind.Function;
      case TIP_TYPE.PROCEDURE:
        return monaco.languages.CompletionItemKind.Unit;
      case TIP_TYPE.KEYWORD:
        return monaco.languages.CompletionItemKind.Keyword;
      case TIP_TYPE.TRIGGER:
        return monaco.languages.CompletionItemKind.Event;
      default:
        return monaco.languages.CompletionItemKind.Text;
    }
  };

  // return <MonacoEditor id={'demo'} />;

  return <SQLEditorDemo />;
};

export default Demo;
