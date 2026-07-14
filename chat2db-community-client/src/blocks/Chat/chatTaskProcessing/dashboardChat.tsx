import chatService, { IQueryTableDataParams } from '@/service/chat';
import miscServices from '@/service/misc';
import { AnswerPartsStatus, AnswerPartsType } from '@/constants/chat';
import { endAnswer } from '@/blocks/Chat/chatTaskProcessing/endAnswer';
import { useChatStore } from '@/store/chat';

export const chatGenerateDashboard = (props: IQueryTableDataParams, questionId, isStep) => {
  const { appendAnswerParts, updateAnswerId } = useChatStore.getState();

  chatService.queryAnswer({ questionId }).then((res) => {
    if (!res?.[0]?.questionId) {
      return;
    }
    const { questionId, id: answerId } = res[0];
    // update answerId
    updateAnswerId(questionId, answerId!);
    const params = {
      ...props,
      answerId,
      questionId,
    };
    if (isStep) {
      // Add loading effect of query data
      appendAnswerParts({
        data: {
          partType: AnswerPartsType.DATA,
          status: AnswerPartsStatus.LOADING,
          step: 2,
        },
        questionId,
      });
      // filter sql
      miscServices.characterHandler({ text: props.sql || '' }).then((filteredSql) => {
        params.sql = filteredSql;
        chatService.queryTableData(params).then((res) => {
          appendAnswerParts({
            data: {
              ...res,
              status: AnswerPartsStatus.FINISH,
              step: 2,
            },
            questionId,
          });

          appendAnswerParts({
            data: {
              partType: AnswerPartsType.DASHBOARD,
              status: AnswerPartsStatus.LOADING,
              step: 3,
            },
            questionId,
          });

          // The third step is to generate a chart
          dashboardChatThirdStep(params, questionId);
        });
      });
    } else {
      endAnswer(questionId, params, 2);
    }
  });
};

// The third step
export const dashboardChatThirdStep = (props: IQueryTableDataParams, questionId) => {
  const { appendAnswerParts } = useChatStore.getState();
  const getRecommendListParams = {
    ...props,
    tableList: undefined,
  };
  chatService.queryChart(props).then((res) => {
    if (!res.partType) {
      appendAnswerParts({
        data: {
          partType: AnswerPartsType.DASHBOARD,
          status: AnswerPartsStatus.FINISH,
          step: 3,
        },
        questionId,
      });
      endAnswer(questionId, getRecommendListParams, 3);
      return;
    }
    appendAnswerParts({
      data: {
        ...res,
        status: AnswerPartsStatus.FINISH,
        step: 3,
      },
      questionId,
    });
    endAnswer(questionId, getRecommendListParams, 4);
  });
};
