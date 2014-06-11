#
# ovirt-engine-setup -- ovirt engine setup
# Copyright (C) 2013 Red Hat, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


"""Clear plugin."""


import os
import shutil
import gettext
_ = lambda m: gettext.dgettext(message=m, domain='ovirt-engine-reports')


from otopi import util
from otopi import plugin


from ovirt_engine_setup import constants as osetupcons
from ovirt_engine_setup.reports import constants as oreportscons
from ovirt_engine_setup import dialog


@util.export
class Plugin(plugin.PluginBase):
    """Clear plugin."""

    def __init__(self, context):
        super(Plugin, self).__init__(context=context)

    @plugin.event(
        stage=plugin.Stages.STAGE_INIT,
    )
    def _init(self):
        self.environment.setdefault(
            oreportscons.RemoveEnv.REMOVE_JASPER_ARTIFACTS,
            None
        )

    @plugin.event(
        stage=plugin.Stages.STAGE_CUSTOMIZATION,
        after=(
            osetupcons.Stages.REMOVE_CUSTOMIZATION_COMMON,
        ),
    )
    def _customization(self):
        if self.environment[
            osetupcons.RemoveEnv.REMOVE_ALL
        ]:
            self.environment[
                oreportscons.RemoveEnv.REMOVE_JASPER_ARTIFACTS
            ] = True

        if self.environment[
            oreportscons.RemoveEnv.REMOVE_JASPER_ARTIFACTS
        ] is None:
            self.environment[
                oreportscons.RemoveEnv.REMOVE_JASPER_ARTIFACTS
            ] = dialog.queryBoolean(
                dialog=self.dialog,
                name='OVESETUP_REPORTS_REMOVE_JASPER_ARTIFACTS',
                note=_(
                    'Do you want to remove Reports Jasper artifacts? '
                    '(@VALUES@) [@DEFAULT@]: '
                ),
                prompt=True,
                true=_('Yes'),
                false=_('No'),
                default=False,
            )

    @plugin.event(
        stage=plugin.Stages.STAGE_MISC,
        condition=lambda self: self.environment[
            oreportscons.RemoveEnv.REMOVE_JASPER_ARTIFACTS
        ],
    )
    def _misc(self):
        for src in (
            oreportscons.FileLocations.
            OVIRT_ENGINE_REPORTS_JASPER_WAR,
            oreportscons.FileLocations.
            OVIRT_ENGINE_REPORTS_JASPER_MODULES,
            oreportscons.FileLocations.
            OVIRT_ENGINE_REPORTS_BUILDOMATIC_CONFIG,
        ):
            if os.path.exists(src):
                shutil.rmtree(src)


# vim: expandtab tabstop=4 shiftwidth=4
