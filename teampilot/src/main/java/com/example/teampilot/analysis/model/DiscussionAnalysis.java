
package com.example.teampilot.analysis.model;

import java.util.List;

public record DiscussionAnalysis(

        String overallSummary,

        List<String> keyTopics,

        List<Decision> decisions,

        List<ActionItem> actionItems,

        List<String> unresolvedIssues

) {

    public DiscussionAnalysis {
        keyTopics = keyTopics == null ? List.of() : List.copyOf(keyTopics);
        decisions = decisions == null ? List.of() : List.copyOf(decisions);
        actionItems = actionItems == null ? List.of() : List.copyOf(actionItems);
        unresolvedIssues = unresolvedIssues == null
                ? List.of()
                : List.copyOf(unresolvedIssues);
    }

    public record Decision(
            String description,
            List<String> sourceMessageIds
    ) {
        public Decision {
            sourceMessageIds = sourceMessageIds == null
                    ? List.of()
                    : List.copyOf(sourceMessageIds);
        }
    }

    public record ActionItem(
            String task,
            String owner,
            String deadline,
            List<String> sourceMessageIds
    ) {
        public ActionItem {
            sourceMessageIds = sourceMessageIds == null
                    ? List.of()
                    : List.copyOf(sourceMessageIds);
        }
    }
}
