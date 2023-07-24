package tech.nmhillusion.pi_logger.listener;

import tech.nmhillusion.pi_logger.model.LogConfigModel;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public interface OnChangeConfig {
    void onChange(LogConfigModel newConfig);
}
