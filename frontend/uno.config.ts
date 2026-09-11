import {
    defineConfig,
    presetAttributify,
    presetIcons,
    presetTypography,
    presetWind3,
    transformerDirectives,
    transformerVariantGroup
} from 'unocss'

export default defineConfig({
    presets: [
        presetWind3(),
        presetAttributify(),
        presetIcons({
            collections: {
                carbon: () => import('@iconify-json/carbon/icons.json').then(i => i.default),
                mdi: () => import('@iconify-json/mdi/icons.json').then(i => i.default),
            }
        }),
        presetTypography(),
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup(),
    ],
})